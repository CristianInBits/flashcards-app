package dev.cristianinbits.flashcards.card.api;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.annotation.Validated;

import dev.cristianinbits.flashcards.card.dto.CardCreateRequest;
import dev.cristianinbits.flashcards.card.dto.CardDto;
import dev.cristianinbits.flashcards.card.dto.CardUpdateRequest;
import dev.cristianinbits.flashcards.card.service.CardService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de tarjetas (Cards).
 *
 * Expone endpoints bajo el prefijo {@code /api} para crear, listar, buscar,
 * obtener, actualizar y eliminar tarjetas. La validación de parámetros está
 * activada con {@code @Validated}.
 *
 * La ordenación permitida en listados se restringe a las propiedades definidas
 * en {@code ALLOWED_SORTS} para evitar ordenaciones no controladas.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class CardController {

    /**
     * Conjunto de propiedades permitidas para ordenación en las consultas de
     * tarjetas.
     */
    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "updatedAt", "front");

    /** Servicio de negocio para operaciones sobre tarjetas. */
    private final CardService service;

    /**
     * Crea una nueva tarjeta.
     *
     * Endpoint: {@code POST /api/cards}
     *
     * @param req datos de creación de la tarjeta
     * @param uri generador de URIs para construir la cabecera Location del recurso
     *            creado
     * @return respuesta con código {@code 201 Created}, cabecera Location y el DTO
     *         de la tarjeta
     * @throws NoSuchElementException si el mazo indicado en la solicitud no existe
     */
    @PostMapping("/cards")
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardCreateRequest req,
            UriComponentsBuilder uri) {
        var created = service.create(req);
        var location = uri.path("/api/cards/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Lista las tarjetas de un mazo con paginación y ordenación.
     *
     * Endpoint: {@code GET /api/decks/{deckId}/cards}
     *
     * @param deckId identificador del mazo
     * @param q      parámetros de paginación y ordenación
     * @return página de tarjetas pertenecientes al mazo indicado
     * @throws NoSuchElementException   si el mazo no existe
     * @throws IllegalArgumentException si el campo de orden indicado no está
     *                                  permitido
     */
    @GetMapping("/decks/{deckId}/cards")
    public Page<CardDto> listByDeck(@PathVariable UUID deckId, @Valid CardsPageQuery q) {
        Pageable pageable = q.toPageable(ALLOWED_SORTS);
        return service.listByDeck(deckId, pageable);
    }

    /**
     * Busca tarjetas dentro de un mazo aplicando filtros de texto y etiqueta.
     *
     * Endpoint: {@code GET /api/decks/{deckId}/cards/search}
     *
     * @param deckId identificador del mazo
     * @param q      parámetros de consulta con paginación, ordenación y filtros
     * @return página de tarjetas que cumplen los criterios de búsqueda
     * @throws NoSuchElementException   si el mazo no existe
     * @throws IllegalArgumentException si el campo de orden indicado no está
     *                                  permitido
     */
    @GetMapping("/decks/{deckId}/cards/search")
    public Page<CardDto> search(@PathVariable UUID deckId, @Valid CardsPageQuery q) {
        Pageable pageable = q.toPageable(ALLOWED_SORTS);
        return service.search(deckId, q.qOrNull(), q.tagOrNull(), pageable);
    }

    /**
     * Obtiene una tarjeta por su identificador.
     *
     * Endpoint: {@code GET /api/cards/{id}}
     *
     * @param id identificador único de la tarjeta
     * @return DTO de la tarjeta solicitada
     * @throws NoSuchElementException si la tarjeta no existe
     */
    @GetMapping("/cards/{id}")
    public CardDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    /**
     * Actualiza una tarjeta existente.
     *
     * Endpoint: {@code PUT /api/cards/{id}}
     *
     * @param id  identificador de la tarjeta a actualizar
     * @param req datos de actualización
     * @return DTO de la tarjeta actualizada
     * @throws NoSuchElementException si la tarjeta no existe
     */
    @PutMapping("/cards/{id}")
    public CardDto update(@PathVariable UUID id, @Valid @RequestBody CardUpdateRequest req) {
        return service.update(id, req);
    }

    /**
     * Elimina una tarjeta por su identificador.
     *
     * Endpoint: {@code DELETE /api/cards/{id}}
     *
     * @param id identificador de la tarjeta a eliminar
     * @throws NoSuchElementException si la tarjeta no existe
     */
    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
