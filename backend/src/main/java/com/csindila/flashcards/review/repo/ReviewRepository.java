package com.csindila.flashcards.review.repo;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.csindila.flashcards.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByDueAtLessThanEqualOrderByDueAtAsc(OffsetDateTime now, Pageable pageable);

    Optional<Review> findById(UUID cardId); // alias semántico

    @Query("""
              SELECT r FROM Review r
              JOIN r.card c
              WHERE r.dueAt <= :now
                AND (:deckId IS NULL OR c.deck.id = :deckId)
              ORDER BY r.dueAt ASC
            """)
    Page<Review> findDueFiltered(
            @Param("now") OffsetDateTime now,
            @Param("deckId") UUID deckId,
            Pageable pageable);

    // Para barajar (aleatorio) – usamos función RANDOM() de Postgres con nativa
    @Query(value = """
              SELECT r.* FROM reviews r
              JOIN cards c ON c.id = r.card_id
              WHERE r.due_at <= :now
                AND (:deckId::uuid IS NULL OR c.deck_id = :deckId)
              ORDER BY random()
              LIMIT 1
            """, nativeQuery = true)
    Review findOneDueRandom(
            @Param("now") OffsetDateTime now,
            @Param("deckId") UUID deckId);
}