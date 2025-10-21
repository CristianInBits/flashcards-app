package com.csindila.flashcards.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.csindila.flashcards.card.repo.CardRepository;
import com.csindila.flashcards.review.domain.Review;
import com.csindila.flashcards.review.domain.ReviewLog;
import com.csindila.flashcards.review.dto.ReviewDto;
import com.csindila.flashcards.review.dto.ReviewQueueItemDto;
import com.csindila.flashcards.review.repo.ReviewLogRepository;
import com.csindila.flashcards.review.repo.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private static final BigDecimal MIN_EASE = BigDecimal.valueOf(1.30);

    private final ReviewRepository reviews;
    private final ReviewLogRepository logs;
    private final CardRepository cards;

    /**
     * Devuelve la cola de tarjetas vencidas (due <= now) limitada.
     */
    @Transactional(readOnly = true)
    public List<ReviewQueueItemDto> getDueQueue(int limit) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var page = reviews.findByDueAtLessThanEqualOrderByDueAtAsc(now, PageRequest.of(0, limit));
        return page.getContent().stream()
                .map(r -> {
                    var c = r.getCard();
                    return new ReviewQueueItemDto(
                            c.getId(), c.getDeck().getId(), c.getFront(), c.getBack(), c.getTags(), c.isLatex());
                })
                .toList();
    }

    /**
     * Registra respuesta para una card (0..5) y recalcula SRS (SM-2).
     */
    public ReviewDto answer(UUID cardId, int grade) {
        if (grade < 0 || grade > 5)
            throw new IllegalArgumentException("grade debe estar entre 0 y 5");

        var card = cards.findById(cardId).orElseThrow(() -> new NoSuchElementException("Card no encontrada"));

        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var review = reviews.findById(cardId).orElseGet(() -> {
            var r = new Review();
            r.setCard(card);
            r.setDueAt(now); // primera vez, due = ahora
            // ease se rellenará en @PrePersist si sigue null
            return r;
        });

        // Copias para log
        var prevDue = review.getDueAt();
        var prevInterval = review.getIntervalDays();
        var prevEaseBD = review.getEase(); // puede ser null si es la 1ª vez
        if (prevEaseBD == null)
            prevEaseBD = new BigDecimal("2.50");

        // ---- SM-2 simplificado ----
        int reps = review.getReps();
        double ease = prevEaseBD.doubleValue();
        int nextInterval;

        if (grade < 3) {
            // lapse
            review.setLapses(review.getLapses() + 1);
            reps = 0;
            ease = Math.max(MIN_EASE.doubleValue(), ease - 0.20);
            nextInterval = 1;
        } else {
            // correcto
            reps = reps + 1;
            if (reps == 1) {
                nextInterval = 1;
            } else if (reps == 2) {
                nextInterval = 6;
            } else {
                nextInterval = Math.max(1, (int) Math.round(review.getIntervalDays() * ease));
            }
            // fórmula SM-2
            ease = ease + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
            ease = Math.max(MIN_EASE.doubleValue(), ease);
        }

        // Guardar con BigDecimal(4,2)
        var easeBD = BigDecimal.valueOf(ease).setScale(2, RoundingMode.HALF_UP);

        review.setReps(reps);
        review.setEase(easeBD);
        review.setIntervalDays(nextInterval);
        review.setLastReviewedAt(now);
        review.setDueAt(now.plusDays(nextInterval));

        reviews.save(review);

        // Log
        var log = new ReviewLog();
        log.setCard(card);
        log.setGrade((short) grade);
        log.setPrevDueAt(prevDue);
        log.setNewDueAt(review.getDueAt());
        log.setPrevInterval(prevInterval == 0 ? null : prevInterval);
        log.setNewInterval(review.getIntervalDays());
        log.setPrevEase(prevEaseBD); // BigDecimal
        log.setNewEase(review.getEase()); // BigDecimal
        logs.save(log);

        return toDto(review);
    }

    @Transactional(readOnly = true)
    public ReviewDto get(UUID cardId) {
        var r = reviews.findById(cardId).orElseThrow(() -> new NoSuchElementException("Review no encontrada"));
        return toDto(r);
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getCard().getId(),
                r.getDueAt(),
                r.getIntervalDays(),
                r.getEase() == null ? 0.0 : r.getEase().doubleValue(),
                r.getReps(),
                r.getLapses(),
                r.getLastReviewedAt());
    }
    

    @Transactional(readOnly = true)
    public ReviewQueueItemDto getNext(UUID deckId, boolean shuffle) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);

        Review r;
        if (shuffle) {
            // Aleatorio (nativa)
            r = reviews.findOneDueRandom(now, deckId);
            if (r == null)
                return null;
        } else {
            // Orden natural por dueAt (asc)
            var page = reviews.findDueFiltered(now, deckId, PageRequest.of(0, 1));
            if (page.isEmpty())
                return null;
            r = page.getContent().get(0);
        }

        var c = r.getCard();
        return new ReviewQueueItemDto(
                c.getId(), c.getDeck().getId(), c.getFront(), c.getBack(), c.getTags(), c.isLatex());
    }

    public ReviewDto skip(UUID cardId, int minutes) {
        if (minutes <= 0)
            minutes = 10; // default
        var review = reviews.findById(cardId)
                .orElseThrow(() -> new NoSuchElementException("Review no encontrada"));
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        // Posponemos sin alterar reps/ease/interval
        review.setDueAt(now.plusMinutes(minutes));
        reviews.save(review);
        return toDto(review);
    }
}