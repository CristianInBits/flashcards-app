package dev.cristianinbits.flashcards.card.service;

import dev.cristianinbits.flashcards.card.domain.Card;
import dev.cristianinbits.flashcards.card.dto.CardCreateRequest;
import dev.cristianinbits.flashcards.card.dto.CardDto;
import dev.cristianinbits.flashcards.card.dto.CardUpdateRequest;
import dev.cristianinbits.flashcards.card.repo.CardRepository;
import dev.cristianinbits.flashcards.deck.repo.DeckRepository;
import dev.cristianinbits.flashcards.review.domain.CardSrsState;
import dev.cristianinbits.flashcards.review.repo.CardSrsStateRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las
 * tarjetas (Cards).
 *
 * Coordina las operaciones entre los repositorios de tarjetas y mazos,
 * validando la existencia de los mazos y aplicando reglas de negocio antes de
 * interactuar con la base de datos.
 *
 * Además, al crear una tarjeta inicializa su estado SRS asociado
 * ({@link CardSrsState}) para que entre inmediatamente en la cola de repaso
 * (con dueAt = now UTC, intervalDays = 0, easeFactor = 2.50, repetitions = 0,
 * lastResult = 0).
 *
 * Anotado con {@code @Service} para su detección por Spring y con
 * {@code @Transactional(readOnly = true)} para indicar que, por defecto, sus
 * métodos no modifican datos salvo los explícitamente anotados con
 * {@code @Transactional}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    /**
     * Repositorio encargado de las operaciones de persistencia sobre las tarjetas.
     */
    private final CardRepository cards;

    /**
     * Repositorio encargado de validar y acceder a los mazos asociados a las
     * tarjetas.
     */
    private final DeckRepository decks;

    /** Repositorio para la persistencia del estado SRS de las tarjetas. */
    private final CardSrsStateRepository srsRepo;

    /**
     * Crea una nueva tarjeta asociada a un mazo existente e inicializa su estado
     * SRS.
     *
     * El estado SRS se crea con valores por defecto para que la tarjeta aparezca
     * desde el inicio en la cola de revisión (dueAt = now UTC, intervalDays = 0,
     * easeFactor = 2.50, repetitions = 0, lastResult = 0).
     *
     * @param req datos de creación de la tarjeta
     * @return DTO con la información de la tarjeta creada
     * @throws NoSuchElementException si el mazo especificado no existe
     */
    @Transactional
    public CardDto create(CardCreateRequest req) {
        var deck = decks.findById(req.deckId())
                .orElseThrow(() -> new NoSuchElementException("Deck no encontrado: " + req.deckId()));

        var c = new Card();
        c.setDeck(deck);
        c.setFront(normalizeOrEmpty(req.front()));
        c.setBack(normalizeOrEmpty(req.back()));
        c.setTags(normalizeOrNull(req.tags()));
        c.setLatex(Boolean.TRUE.equals(req.latex()));

        var srs = new CardSrsState();
        srs.setCardId(c.getId());
        srs.setCard(c);
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        srs.setDueAt(now);
        srs.setIntervalDays(0);
        srs.setEaseFactor(new BigDecimal("2.50"));
        srs.setRepetitions(0);
        srs.setLastResult((short) 0);
        srs.setUpdatedAt(now);
        srsRepo.save(srs);

        c = cards.save(c);
        return toDto(c);
    }

    /**
     * Lista las tarjetas pertenecientes a un mazo concreto con soporte de
     * paginación.
     *
     * @param deckId   identificador del mazo
     * @param pageable parámetros de paginación y ordenación
     * @return página de tarjetas del mazo indicado
     * @throws NoSuchElementException si el mazo no existe
     */
    public Page<CardDto> listByDeck(UUID deckId, Pageable pageable) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado: " + deckId);
        }
        return cards.findByDeck_Id(deckId, pageable).map(this::toDto);
    }

    /**
     * Realiza una búsqueda filtrada de tarjetas dentro de un mazo.
     * 
     * Permite aplicar filtros por texto (en el anverso o reverso) y por etiquetas,
     * de forma insensible a mayúsculas y minúsculas.
     *
     * @param deckId   identificador del mazo
     * @param q        texto de búsqueda parcial (puede ser nulo o vacío)
     * @param tag      etiqueta de filtrado (puede ser nula o vacía)
     * @param pageable parámetros de paginación y ordenación
     * @return página de tarjetas que cumplen los criterios de búsqueda
     * @throws NoSuchElementException si el mazo no existe
     */
    public Page<CardDto> search(UUID deckId, String q, String tag, Pageable pageable) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado: " + deckId);
        }

        String qPattern = isBlank(q) ? null : "%" + q.toLowerCase() + "%";
        String tagPattern = isBlank(tag) ? null : "%" + tag.toLowerCase() + "%";

        return cards.search(deckId, qPattern, tagPattern, pageable).map(this::toDto);
    }

    /**
     * Obtiene una tarjeta a partir de su identificador único.
     *
     * @param id identificador de la tarjeta
     * @return DTO con los datos de la tarjeta encontrada
     * @throws NoSuchElementException si no existe una tarjeta con el identificador
     *                                indicado
     */
    public CardDto get(UUID id) {
        return toDto(findOr404(id));
    }

    /**
     * Actualiza una tarjeta existente con los datos proporcionados.
     *
     * @param id  identificador de la tarjeta a actualizar
     * @param req datos de actualización
     * @return DTO con la información actualizada de la tarjeta
     * @throws NoSuchElementException si la tarjeta no existe
     */
    @Transactional
    public CardDto update(UUID id, CardUpdateRequest req) {
        var c = findOr404(id);
        c.setFront(normalizeOrEmpty(req.front()));
        c.setBack(normalizeOrEmpty(req.back()));
        c.setTags(normalizeOrNull(req.tags()));
        if (req.latex() != null) {
            c.setLatex(req.latex());
        }
        return toDto(c);
    }

    /**
     * Elimina una tarjeta existente de la base de datos.
     *
     * @param id identificador de la tarjeta a eliminar
     * @throws NoSuchElementException si la tarjeta no existe
     */
    @Transactional
    public void delete(UUID id) {
        var c = findOr404(id);
        cards.delete(c);
    }

    /**
     * Convierte una entidad {@link Card} en su representación de transferencia de
     * datos ({@link CardDto}).
     *
     * @param c entidad Card
     * @return DTO con los datos equivalentes de la tarjeta
     */
    private CardDto toDto(Card c) {
        return new CardDto(
                c.getId(),
                c.getDeck().getId(),
                c.getFront(),
                c.getBack(),
                c.getTags(),
                c.isLatex(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }

    /**
     * Busca una tarjeta por su identificador o lanza una excepción si no se
     * encuentra.
     *
     * @param id identificador único de la tarjeta
     * @return entidad {@link Card} encontrada
     * @throws NoSuchElementException si la tarjeta no existe
     */
    private Card findOr404(UUID id) {
        return cards.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card no encontrado: " + id));
    }

    /**
     * Verifica si una cadena es nula o está compuesta únicamente por espacios en
     * blanco.
     *
     * @param s cadena a evaluar
     * @return {@code true} si la cadena es nula o vacía; {@code false} en caso
     *         contrario
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Normaliza una cadena eliminando espacios en blanco y devolviendo una cadena
     * vacía si es nula.
     *
     * @param s texto a normalizar
     * @return cadena normalizada o vacía
     */
    private static String normalizeOrEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
     * Normaliza una cadena eliminando espacios en blanco y devolviendo {@code null}
     * si queda vacía.
     *
     * @param s texto a normalizar
     * @return cadena normalizada o {@code null} si está vacía
     */
    private static String normalizeOrNull(String s) {
        if (s == null)
            return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
