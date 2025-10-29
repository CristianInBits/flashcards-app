package dev.cristianinbits.flashcards.review.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import dev.cristianinbits.flashcards.card.dto.CardDto;
import dev.cristianinbits.flashcards.common.web.PageQuery;
import dev.cristianinbits.flashcards.review.dto.ReviewEventCreateRequest;
import dev.cristianinbits.flashcards.review.dto.ReviewEventDto;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionCreateRequest;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionDto;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionFinishRequest;
import dev.cristianinbits.flashcards.review.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para operaciones de repaso (review).
 *
 * Expone endpoints bajo el prefijo {@code /api} para:
 * - Obtener tarjetas vencidas (due) por mazo.
 * - Gestionar sesiones de repaso (crear, listar, obtener y finalizar).
 * - Registrar eventos de revisión, tanto dentro de una sesión como de forma
 * independiente.
 *
 * La validación de parámetros está habilitada mediante {@code @Validated}. La
 * ordenación permitida para sesiones se restringe a las propiedades definidas
 * en {@code ALLOWED_REVIEW_SORT}.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    /** Propiedades permitidas para ordenación en listados de sesiones. */
    private static final Set<String> ALLOWED_REVIEW_SORT = Set.of("startedAt");

    /** Servicio de negocio que orquesta la lógica de repaso. */
    private final ReviewService service;

    // ------------------------------------------------------------
    // 1) Due cards por deck
    // ------------------------------------------------------------

    /**
     * Devuelve hasta {@code limit} tarjetas vencidas (due) para el mazo indicado.
     *
     * Endpoint: {@code GET /api/decks/{deckId}/cards/due}
     *
     * @param deckId identificador del mazo
     * @param limit  número máximo de tarjetas a devolver, entre 1 y 100 (por
     *               defecto 20)
     * @return lista de tarjetas pendientes de repaso en formato DTO
     * @throws NoSuchElementException si el mazo no existe
     */
    @GetMapping("/decks/{deckId}/cards/due")
    public List<CardDto> dueCards(
            @PathVariable UUID deckId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        return service.dueCards(deckId, limit);
    }

    // ------------------------------------------------------------
    // 2) Sesiones (crear, listar, obtener, finalizar)
    // ------------------------------------------------------------

    /**
     * Crea una nueva sesión de repaso para el mazo especificado.
     * Devuelve {@code 201 Created} con cabecera Location apuntando al recurso
     * creado.
     *
     * Endpoint: {@code POST /api/decks/{deckId}/reviews}
     *
     * @param deckId identificador del mazo
     * @param req    datos opcionales de creación de sesión; si no se incluye, se
     *               usa la hora actual
     * @param uri    generador de URIs para construir la cabecera Location
     * @return respuesta con {@code 201 Created} y el DTO de la sesión creada
     * @throws NoSuchElementException si el mazo no existe
     */
    @PostMapping("/decks/{deckId}/reviews")
    public ResponseEntity<ReviewSessionDto> createSession(
            @PathVariable UUID deckId,
            @Valid @RequestBody(required = false) ReviewSessionCreateRequest req,
            UriComponentsBuilder uri) {
        var created = service.createSession(deckId, req);
        var location = uri.path("/api/reviews/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Lista las sesiones de repaso de un mazo con paginación.
     * Si no se indica orden, se aplica por defecto {@code startedAt DESC}.
     *
     * Endpoint: {@code GET /api/decks/{deckId}/reviews}
     *
     * @param deckId identificador del mazo
     * @param q      parámetros de paginación y ordenación
     * @return página de sesiones en formato DTO
     * @throws NoSuchElementException   si el mazo no existe
     * @throws IllegalArgumentException si la propiedad de orden no está permitida
     */
    @GetMapping("/decks/{deckId}/reviews")
    public Page<ReviewSessionDto> listSessionsByDeck(
            @PathVariable UUID deckId,
            @Valid PageQuery q) {
        Pageable pageable = q.toPageable(ALLOWED_REVIEW_SORT);
        return service.listSessionsByDeck(deckId, pageable);
    }

    /**
     * Obtiene una sesión de repaso por su identificador.
     *
     * Endpoint: {@code GET /api/reviews/{id}}
     *
     * @param id identificador de la sesión
     * @return DTO de la sesión solicitada
     * @throws NoSuchElementException si la sesión no existe
     */
    @GetMapping("/reviews/{id}")
    public ReviewSessionDto getSession(@PathVariable UUID id) {
        return service.getSession(id);
    }

    /**
     * Finaliza una sesión de repaso, estableciendo marca de fin y contadores.
     *
     * Endpoint: {@code PUT /api/reviews/{id}/finish}
     *
     * @param id  identificador de la sesión a finalizar
     * @param req datos de finalización (contadores y duración)
     * @return DTO de la sesión finalizada
     * @throws NoSuchElementException   si la sesión no existe
     * @throws IllegalStateException    si la sesión ya estaba finalizada
     * @throws IllegalArgumentException si los contadores son inconsistentes
     */
    @PutMapping("/reviews/{id}/finish")
    public ReviewSessionDto finishSession(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewSessionFinishRequest req) {
        return service.finishSession(id, req);
    }

    // ------------------------------------------------------------
    // 3) Registrar evento (dos estilos)
    // ------------------------------------------------------------

    /**
     * Registra un evento de repaso dentro de una sesión existente.
     * El {@code reviewId} prevalece sobre el incluido en el cuerpo si ambos se
     * envían.
     *
     * Endpoint: {@code POST /api/reviews/{reviewId}/events}
     *
     * @param reviewId identificador de la sesión existente
     * @param body     datos del evento a registrar (contiene {@code cardId},
     *                 resultado y tiempo)
     * @return DTO del evento registrado
     * @throws NoSuchElementException   si la sesión o la tarjeta no existen
     * @throws IllegalStateException    si la sesión ya está finalizada
     * @throws IllegalArgumentException si la tarjeta no pertenece al mazo de la
     *                                  sesión
     * @throws OptimisticLockException  si se produce un conflicto de concurrencia
     *                                  al actualizar SRS
     */
    @PostMapping("/reviews/{reviewId}/events")
    public ReviewEventDto registerEventInSession(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewEventCreateRequest body) {
        var req = new ReviewEventCreateRequest(
                reviewId,
                body.cardId(),
                body.result(),
                body.elapsedMs());
        return service.registerEvent(req);
    }

    /**
     * Registra un evento de repaso para una tarjeta concreta sin requerir sesión.
     * Si el cuerpo incluye {@code reviewId}, se asociará; en caso contrario, el
     * evento quedará sin sesión.
     * El {@code cardId} de la ruta prevalece sobre el del cuerpo si ambos se
     * envían.
     *
     * Endpoint: {@code POST /api/cards/{cardId}/review}
     *
     * @param cardId identificador de la tarjeta
     * @param body   datos del evento a registrar
     * @return DTO del evento registrado
     * @throws NoSuchElementException   si la tarjeta no existe o si el
     *                                  {@code reviewId} indicado no existe
     * @throws IllegalStateException    si la sesión indicada está finalizada
     * @throws IllegalArgumentException si la tarjeta no pertenece al mazo de la
     *                                  sesión indicada
     * @throws OptimisticLockException  si se produce un conflicto de concurrencia
     *                                  al actualizar SRS
     */
    @PostMapping("/cards/{cardId}/review")
    public ReviewEventDto registerEventForCard(
            @PathVariable UUID cardId,
            @Valid @RequestBody ReviewEventCreateRequest body) {
        var req = new ReviewEventCreateRequest(
                body.reviewId(),
                cardId,
                body.result(),
                body.elapsedMs());
        return service.registerEvent(req);
    }
}