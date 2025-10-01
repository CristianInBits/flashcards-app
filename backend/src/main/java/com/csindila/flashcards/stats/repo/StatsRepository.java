package com.csindila.flashcards.stats.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de solo lectura para agregaciones de estadísticas.
 * Usamos consultas nativas Postgres para agrupar por día con date_trunc.
 */
public interface StatsRepository extends Repository<Object, UUID> {

    // === Global ===
    @Query(value = "select count(*) from decks", nativeQuery = true)
    long countAllDecks();

    @Query(value = "select count(*) from cards", nativeQuery = true)
    long countAllCards();

    @Query(value = "select count(*) from reviews r where r.due_at <= :endOfToday", nativeQuery = true)
    long countDueToday(@Param("endOfToday") OffsetDateTime endOfToday);

    @Query(value = "select count(*) from review_log rl where rl.reviewed_at >= :startOfToday and rl.reviewed_at <= :endOfToday", nativeQuery = true)
    long countReviewedToday(@Param("startOfToday") OffsetDateTime startOfToday,
            @Param("endOfToday") OffsetDateTime endOfToday);

    @Query(value = """
            select date_trunc('day', rl.reviewed_at) as day, count(*) as cnt
            from review_log rl
            where rl.reviewed_at >= :since
            group by day
            order by day
            """, nativeQuery = true)
    List<Object[]> countDailySince(@Param("since") OffsetDateTime since);

    // === Por deck ===
    @Query(value = "select count(*) from cards c where c.deck_id = :deckId", nativeQuery = true)
    long countCardsByDeck(@Param("deckId") UUID deckId);

    @Query(value = """
            select count(*)
            from reviews r
            join cards c on c.id = r.card_id
            where c.deck_id = :deckId and r.due_at <= :endOfToday
            """, nativeQuery = true)
    long countDueTodayByDeck(@Param("deckId") UUID deckId,
            @Param("endOfToday") OffsetDateTime endOfToday);

    @Query(value = """
            select count(*)
            from review_log rl
            join cards c on c.id = rl.card_id
            where c.deck_id = :deckId
              and rl.reviewed_at >= :startOfToday and rl.reviewed_at <= :endOfToday
            """, nativeQuery = true)
    long countReviewedTodayByDeck(@Param("deckId") UUID deckId,
            @Param("startOfToday") OffsetDateTime startOfToday,
            @Param("endOfToday") OffsetDateTime endOfToday);

    @Query(value = """
            select date_trunc('day', rl.reviewed_at) as day, count(*) as cnt
            from review_log rl
            join cards c on c.id = rl.card_id
            where c.deck_id = :deckId and rl.reviewed_at >= :since
            group by day
            order by day
            """, nativeQuery = true)
    List<Object[]> countDailySinceByDeck(@Param("deckId") UUID deckId,
            @Param("since") OffsetDateTime since);
}
