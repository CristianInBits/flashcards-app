package dev.cristianinbits.flashcards.deck.api;

import dev.cristianinbits.flashcards.deck.dto.*;
import dev.cristianinbits.flashcards.common.web.PageQuery;
import dev.cristianinbits.flashcards.deck.service.DeckService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;
import java.util.UUID;

/**
 * Controlador REST para la gestión de mazos (Decks).
 * 
 * Expone los endpoints del recurso {@code /api/decks} y delega la lógica de
 * negocio
 * en la capa de servicio {@link DeckService}. Permite crear, listar, obtener,
 * actualizar y eliminar mazos.
 * 
 * Los métodos están documentados con las anotaciones estándar de Spring MVC y
 * utilizan
 * validación automática de datos de entrada mediante {@code @Valid}.
 */
@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    /**
     * Conjunto de propiedades permitidas para ordenación en consultas paginadas.
     */
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "name");

    /** Servicio encargado de la lógica de negocio relacionada con los mazos. */
    private final DeckService service;

    /**
     * Crea un nuevo mazo a partir de los datos enviados en la solicitud.
     * 
     * @param req datos de creación del mazo
     * @param uri constructor de componentes URI utilizado para generar la ubicación
     *            del nuevo recurso
     * @return respuesta con código {@code 201 Created} y el DTO del mazo creado
     */
    @PostMapping
    public ResponseEntity<DeckDto> create(@Valid @RequestBody DeckCreateRequest req,
            UriComponentsBuilder uri) {
        var created = service.create(req);
        var location = uri.path("/api/decks/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Lista los mazos existentes con soporte de paginación y ordenación.
     * 
     * @param q parámetros de paginación y ordenación validados
     * @return página de mazos representados como DTOs
     */
    @GetMapping
    public Page<DeckDto> list(@Valid PageQuery q) {
        Pageable pageable = q.toPageable(ALLOWED_SORT);
        return service.list(pageable);
    }

    /**
     * Obtiene un mazo específico por su identificador.
     * 
     * @param id identificador único del mazo
     * @return DTO con los datos del mazo solicitado
     * @throws NoSuchElementException si no existe un mazo con el identificador
     *                                indicado
     */
    @GetMapping("/{id}")
    public DeckDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    /**
     * Actualiza un mazo existente con los datos proporcionados.
     * 
     * @param id  identificador del mazo a actualizar
     * @param req datos actualizados del mazo
     * @return DTO del mazo actualizado
     * @throws NoSuchElementException si el mazo no existe
     */
    @PutMapping("/{id}")
    public DeckDto update(@PathVariable UUID id, @Valid @RequestBody DeckUpdateRequest req) {
        return service.update(id, req);
    }

    /**
     * Elimina un mazo existente de la base de datos.
     * 
     * @param id identificador del mazo a eliminar
     * @throws NoSuchElementException si el mazo no existe
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
