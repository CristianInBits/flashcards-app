package dev.cristianinbits.flashcards.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import static org.springframework.data.domain.Sort.Direction.DESC;

import org.springframework.lang.Nullable;

import dev.cristianinbits.flashcards.card.domain.Card;
import dev.cristianinbits.flashcards.card.dto.CardDto;
import dev.cristianinbits.flashcards.card.repo.CardRepository;
import dev.cristianinbits.flashcards.deck.repo.DeckRepository;
import dev.cristianinbits.flashcards.review.domain.CardSrsState;
import dev.cristianinbits.flashcards.review.domain.ReviewEvent;
import dev.cristianinbits.flashcards.review.domain.ReviewSession;
import dev.cristianinbits.flashcards.review.dto.ReviewEventCreateRequest;
import dev.cristianinbits.flashcards.review.dto.ReviewEventDto;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionCreateRequest;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionDto;
import dev.cristianinbits.flashcards.review.dto.ReviewSessionFinishRequest;
import dev.cristianinbits.flashcards.review.repo.CardSrsStateRepository;
import dev.cristianinbits.flashcards.review.repo.ReviewEventRepository;
import dev.cristianinbits.flashcards.review.repo.ReviewSessionRepository;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Servicio de lógica de negocio para el bloque de repaso (review).
 *
 * Orquesta la obtención de tarjetas vencidas (due), la gestión opcional de
 * sesiones de repaso y el registro de eventos de revisión con actualización del
 * estado SRS.
 *
 * Anotado con {@code @Service} para su detección por Spring y con
 * {@code @Transactional(readOnly = true)} para indicar que, por defecto, no
 * modifica datos salvo en los métodos marcados explícitamente con
 * {@code @Transactional}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    /** Repositorio de tarjetas. */
    private final CardRepository cards;
    /** Repositorio de mazos. */
    private final DeckRepository decks;
    /** Repositorio del estado SRS de las tarjetas. */
    private final CardSrsStateRepository srsRepo;
    /** Repositorio de sesiones de repaso. */
    private final ReviewSessionRepository sessions;
    /** Repositorio de eventos de repaso. */
    private final ReviewEventRepository events;

    // =========================
    // 1) DUE CARDS POR DECK
    // =========================

    /**
     * Devuelve un listado de tarjetas pendientes de repaso (due) para un mazo,
     * ordenadas por fecha de vencimiento ascendente y acotadas por un límite.
     *
     * @param deckId identificador del mazo
     * @param limit  número máximo de tarjetas a devolver; se acota a [1, 100]
     * @return lista de tarjetas en formato DTO
     * @throws NoSuchElementException si el mazo no existe
     */
    public List<CardDto> dueCards(UUID deckId, int limit) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado: " + deckId);
        }
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var page = PageRequest.of(0, Math.max(1, Math.min(limit, 100)));
        Page<Card> result = srsRepo.findDueCardsByDeck(deckId, now, page);
        return result.stream().map(this::toCardDto).collect(Collectors.toList());
    }

    // =========================
    // 2) SESIONES (opcional)
    // =========================

    /**
     * Crea una nueva sesión de repaso para un mazo. La fecha de inicio puede venir
     * en la solicitud; si no se indica, se establecerá en {@code @PrePersist}.
     *
     * @param deckId identificador del mazo
     * @param req    datos opcionales de creación de sesión
     * @return DTO de la sesión creada
     * @throws NoSuchElementException si el mazo no existe
     */
    @Transactional
    public ReviewSessionDto createSession(UUID deckId, @Nullable ReviewSessionCreateRequest req) {
        var deck = decks.findById(deckId)
                .orElseThrow(() -> new NoSuchElementException("Deck no encontrado: " + deckId));

        var s = new ReviewSession();
        s.setDeck(deck);

        // Si el cliente envía startedAt, se usa (ya validado como pasado/presente).
        // Si no, lo dejamos null. @PrePersist pondrá now(UTC).
        if (req != null && req.startedAt() != null) {
            s.setStartedAt(req.startedAt().withOffsetSameInstant(ZoneOffset.UTC));
        }

        s = sessions.save(s);
        return toDto(s);
    }

    /**
     * Sobrecarga conveniente para crear una nueva sesión con la hora actual.
     *
     * @param deckId identificador del mazo
     * @return DTO de la sesión creada
     * @throws NoSuchElementException si el mazo no existe
     */
    @Transactional
    public ReviewSessionDto createSession(UUID deckId) {
        return createSession(deckId, null);
    }

    /**
     * Finaliza una sesión de repaso, actualizando marcas temporales y métricas.
     *
     * @param sessionId identificador de la sesión a finalizar
     * @param req       datos de finalización (contadores y tiempo)
     * @return DTO de la sesión finalizada
     * @throws NoSuchElementException   si la sesión no existe
     * @throws IllegalStateException    si la sesión ya estaba finalizada
     * @throws IllegalArgumentException si los contadores son inconsistentes
     */
    @Transactional
    public ReviewSessionDto finishSession(UUID sessionId, ReviewSessionFinishRequest req) {
        var s = sessions.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("ReviewSession no encontrada: " + sessionId));

        if (s.getEndedAt() != null) {
            throw new IllegalStateException("La sesión ya está finalizada");
        }

        var ended = (req.endedAt() != null)
                ? req.endedAt().withOffsetSameInstant(ZoneOffset.UTC)
                : nowUtc();
        s.setEndedAt(ended);

        s.setTotalCards(req.totalCards());
        s.setCorrect(req.correct());
        s.setIncorrect(req.incorrect());
        s.setDurationSec(req.durationSec());

        if (req.totalCards() != req.correct() + req.incorrect()) {
            throw new IllegalArgumentException("totalCards debe ser igual a correct + incorrect");
        }

        return toDto(s);
    }

    /**
     * Lista las sesiones de repaso de un mazo con paginación. Si no se especifica
     * orden, se aplica por defecto {@code startedAt DESC}.
     *
     * @param deckId   identificador del mazo
     * @param pageable parámetros de paginación y ordenación; si es nulo o sin
     *                 orden, se aplica por defecto
     * @return página de sesiones en formato DTO
     * @throws NoSuchElementException si el mazo no existe
     */
    public Page<ReviewSessionDto> listSessionsByDeck(UUID deckId, Pageable pageable) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado: " + deckId);
        }
        var p = (pageable == null) ? PageRequest.of(0, 20, Sort.by(DESC, "startedAt")) : pageable;
        if (p.getSort().isUnsorted()) {
            p = PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by(DESC, "startedAt"));
        }
        return sessions.findByDeck_Id(deckId, p).map(this::toDto);
    }

    /**
     * Obtiene una sesión de repaso por su identificador.
     *
     * @param id identificador de la sesión
     * @return DTO de la sesión encontrada
     * @throws NoSuchElementException si la sesión no existe
     */
    public ReviewSessionDto getSession(UUID id) {
        var s = sessions.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReviewSession no encontrada: " + id));
        return toDto(s);
    }

    // =========================
    // 3) EVENTO + ACTUALIZACIÓN SRS
    // =========================

    /**
     * Registra un evento de repaso para una tarjeta, actualizando su estado SRS.
     * Implementa reintentos frente a conflictos de concurrencia optimista.
     *
     * Estrategia:
     * - Hasta 3 intentos si se produce
     * {@link ObjectOptimisticLockingFailureException} o
     * {@link OptimisticLockException}.
     * - En cada intento se vuelve a cargar y aplicar la regla sobre el estado SRS.
     *
     * @param req solicitud de creación de evento de repaso
     * @return DTO del evento registrado
     * @throws NoSuchElementException   si la tarjeta no existe o si la sesión
     *                                  indicada no existe
     * @throws IllegalStateException    si la sesión indicada está finalizada
     * @throws IllegalArgumentException si la tarjeta no pertenece al mismo mazo que
     *                                  la sesión
     * @throws OptimisticLockException  si tras agotar reintentos persiste el
     *                                  conflicto de concurrencia
     */
    @Transactional
    public ReviewEventDto registerEvent(ReviewEventCreateRequest req) {
        int attempts = 0;
        final int maxAttempts = 3;

        while (true) {
            try {
                return registerEventOnce(req);
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException ex) {
                if (++attempts >= maxAttempts) {
                    throw ex;
                }
            }
        }
    }

    /**
     * Lógica de un único intento para registrar un evento y actualizar el estado
     * SRS.
     * No aplica reintentos; la gestión de reintentos la realiza
     * {@link #registerEvent(ReviewEventCreateRequest)}.
     *
     * @param req solicitud de creación de evento
     * @return DTO del evento creado
     */
    private ReviewEventDto registerEventOnce(ReviewEventCreateRequest req) {
        final var now = nowUtc();

        // 1) Card
        var card = cards.findById(req.cardId())
                .orElseThrow(() -> new NoSuchElementException("Card no encontrada: " + req.cardId()));

        // 2) Session (opcional) + validaciones de dominio
        ReviewSession session = null;
        if (req.reviewId() != null) {
            session = sessions.findById(req.reviewId())
                    .orElseThrow(() -> new NoSuchElementException("ReviewSession no encontrada: " + req.reviewId()));

            if (session.getEndedAt() != null) {
                throw new IllegalStateException("La sesión ya está finalizada");
            }
            if (!session.getDeck().getId().equals(card.getDeck().getId())) {
                throw new IllegalArgumentException("La card no pertenece al mismo deck de la sesión");
            }
        }

        // 3) SRS: carga o bootstrap inicial (se recomienda @Version en la entidad)
        var srs = srsRepo.findById(card.getId()).orElseGet(() -> {
            var st = new CardSrsState();
            st.setCardId(card.getId());
            st.setCard(card);
            st.setDueAt(now);
            st.setIntervalDays(0);
            st.setEaseFactor(new BigDecimal("2.50"));
            st.setRepetitions(0);
            st.setLastResult((short) 0);
            st.setUpdatedAt(now);
            return st;
        });

        // Snapshots previos
        var prevDue = srs.getDueAt();
        var prevInt = srs.getIntervalDays();
        var prevEase = srs.getEaseFactor();

        // 4) Regla SRS (acierto/fallo)
        final boolean isCorrect = req.result() == 1;
        if (isCorrect) {
            srs.setRepetitions(srs.getRepetitions() + 1);
            int newInterval;
            if (srs.getRepetitions() == 1)
                newInterval = 1;
            else if (srs.getRepetitions() == 2)
                newInterval = 3;
            else
                newInterval = Math.max(1, BigDecimal.valueOf(srs.getIntervalDays())
                        .multiply(srs.getEaseFactor())
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue());
            srs.setIntervalDays(newInterval);

            var newEase = minBD(prevEase.add(new BigDecimal("0.05")), new BigDecimal("3.00"));
            srs.setEaseFactor(newEase);
            srs.setLastResult((short) 1);
        } else {
            srs.setRepetitions(0);
            srs.setIntervalDays(1);
            var newEase = maxBD(prevEase.subtract(new BigDecimal("0.15")), new BigDecimal("1.30"));
            srs.setEaseFactor(newEase);
            srs.setLastResult((short) 0);
        }

        var newDue = now.plusDays(srs.getIntervalDays());
        srs.setDueAt(newDue);
        srs.setUpdatedAt(now);

        // 5) Event
        var ev = new ReviewEvent();
        ev.setReview(session);
        ev.setCard(card);
        ev.setAnsweredAt(now);
        ev.setResult(req.result());
        ev.setElapsedMs(Math.max(0, req.elapsedMs()));
        ev.setPrevDueAt(prevDue);
        ev.setNewDueAt(newDue);
        ev.setPrevInterval(prevInt);
        ev.setNewInterval(srs.getIntervalDays());
        ev.setPrevEase(prevEase);
        ev.setNewEase(srs.getEaseFactor());

        // 6) Persistir atómicamente
        srsRepo.save(srs);
        ev = events.save(ev);

        // 7) Counters de sesión (si aplica)
        if (session != null) {
            session.setTotalCards(session.getTotalCards() + 1);
            if (isCorrect)
                session.setCorrect(session.getCorrect() + 1);
            else
                session.setIncorrect(session.getIncorrect() + 1);
        }

        return toDto(ev);
    }

    // =========================
    // Helpers de mapeo/tiempo
    // =========================

    /** @return instante actual en UTC. */
    private static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    /** @return mínimo entre dos {@link BigDecimal}. */
    private static BigDecimal minBD(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    /** @return máximo entre dos {@link BigDecimal}. */
    private static BigDecimal maxBD(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    /**
     * Convierte una entidad {@link Card} a su DTO.
     *
     * @param c entidad de tarjeta
     * @return DTO de tarjeta
     */
    private CardDto toCardDto(Card c) {
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
     * Convierte una entidad {@link ReviewSession} a su DTO.
     *
     * @param s sesión de repaso
     * @return DTO de sesión
     */
    private ReviewSessionDto toDto(ReviewSession s) {
        return new ReviewSessionDto(
                s.getId(),
                s.getDeck().getId(),
                s.getStartedAt(),
                s.getEndedAt(),
                s.getTotalCards(),
                s.getCorrect(),
                s.getIncorrect(),
                s.getDurationSec());
    }

    /**
     * Convierte una entidad {@link ReviewEvent} a su DTO.
     *
     * @param e evento de repaso
     * @return DTO de evento
     */
    private ReviewEventDto toDto(ReviewEvent e) {
        return new ReviewEventDto(
                e.getId(),
                e.getReview() != null ? e.getReview().getId() : null,
                e.getCard().getId(),
                e.getAnsweredAt(),
                e.getResult(),
                e.getElapsedMs(),
                e.getPrevDueAt(),
                e.getNewDueAt(),
                e.getPrevInterval(),
                e.getNewInterval(),
                e.getPrevEase(),
                e.getNewEase());
    }
}
