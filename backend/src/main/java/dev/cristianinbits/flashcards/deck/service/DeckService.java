package dev.cristianinbits.flashcards.deck.service;

import dev.cristianinbits.flashcards.deck.dto.DeckCreateRequest;
import dev.cristianinbits.flashcards.deck.dto.DeckDto;
import dev.cristianinbits.flashcards.deck.dto.DeckUpdateRequest;
import dev.cristianinbits.flashcards.deck.repo.DeckRepository;
import dev.cristianinbits.flashcards.deck.model.Deck;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los
 * mazos (Decks).
 * 
 * Coordina las operaciones entre el repositorio, la base de datos y los DTOs,
 * aplicando las reglas necesarias para la creación, actualización, obtención,
 * listado y eliminación de mazos.
 * 
 * Anotado con {@code @Service} para que Spring lo detecte como componente de
 * servicio, y con {@code @Transactional(readOnly = true)} para indicar que, por
 * defecto, sus métodos no modifican datos salvo aquellos que se marcan
 * explícitamente con {@code @Transactional}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckService {

    /**
     * Repositorio encargado del acceso a la base de datos para la entidad
     * {@link Deck}.
     * Se inyecta automáticamente mediante el constructor generado por Lombok
     * (@RequiredArgsConstructor).
     */
    private final DeckRepository repo;

    /**
     * Crea un nuevo mazo (Deck) a partir de los datos recibidos en la solicitud.
     * Normaliza las cadenas de texto y guarda el nuevo registro en la base de
     * datos.
     *
     * @param req datos de creación del mazo
     * @return DTO con la información del mazo creado
     */
    @Transactional
    public DeckDto create(DeckCreateRequest req) {
        var d = new Deck();
        d.setName(normalize(req.name()));
        d.setDescription(normalize(req.description()));
        d = repo.save(d);
        return toDto(d);
    }

    /**
     * Lista los mazos existentes con soporte de paginación.
     *
     * @param pageable parámetros de paginación y ordenación
     * @return página de mazos representados como DTOs
     */
    public Page<DeckDto> list(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDto);
    }

    /**
     * Obtiene un mazo específico a partir de su identificador.
     *
     * @param id identificador único del mazo
     * @return DTO con la información del mazo
     * @throws NoSuchElementException si el mazo no existe
     */
    public DeckDto get(UUID id) {
        return toDto(findOr404(id));
    }

    /**
     * Actualiza los datos de un mazo existente.
     * 
     * No es necesario invocar {@code save()}, ya que JPA aplicará los cambios
     * automáticamente mediante el mecanismo de dirty checking al confirmar la
     * transacción.
     *
     * @param id  identificador del mazo a actualizar
     * @param req datos de actualización
     * @return DTO actualizado del mazo
     * @throws NoSuchElementException si el mazo no existe
     */
    @Transactional
    public DeckDto update(UUID id, DeckUpdateRequest req) {
        var d = findOr404(id);
        d.setName(normalize(req.name()));
        d.setDescription(normalize(req.description()));
        return toDto(d);
    }

    /**
     * Elimina un mazo de la base de datos a partir de su identificador.
     *
     * @param id identificador único del mazo a eliminar
     * @throws NoSuchElementException si el mazo no existe
     */
    @Transactional
    public void delete(UUID id) {
        var d = findOr404(id);
        repo.delete(d);
    }

    /**
     * Convierte una entidad {@link Deck} a su correspondiente DTO.
     *
     * @param d entidad Deck
     * @return DTO equivalente
     */
    private DeckDto toDto(Deck d) {
        return new DeckDto(
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getCreatedAt());
    }

    /**
     * Busca un mazo por su identificador o lanza una excepción si no existe.
     *
     * @param id identificador único del mazo
     * @return entidad Deck encontrada
     * @throws NoSuchElementException si no se encuentra ningún mazo con el ID
     *                                proporcionado
     */
    private Deck findOr404(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Deck no encontrado: " + id));
    }

    /**
     * Normaliza una cadena de texto eliminando espacios en blanco al inicio y al
     * final.
     * Si la cadena es nula o vacía, devuelve null.
     *
     * @param s texto a normalizar
     * @return texto normalizado o vacío
     */
    private static String normalize(String s) {
        if (s == null)
            return null;
        var trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
