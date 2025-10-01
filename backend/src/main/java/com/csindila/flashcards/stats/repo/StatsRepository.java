package com.csindila.flashcards.stats.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class StatsRepository {

        @PersistenceContext
        private EntityManager em;

        // === Global ===

        public long countAllDecks() {
                var q = em.createNativeQuery("select count(*) from decks");
                return ((Number) q.getSingleResult()).longValue();
                // Puede venir como BigInteger o Long según el driver
        }

        public long countAllCards() {
                var q = em.createNativeQuery("select count(*) from cards");
                return ((Number) q.getSingleResult()).longValue();
        }

        public long countDueToday(OffsetDateTime endOfToday) {
                var q = em.createNativeQuery("""
                                select count(*) from reviews r
                                where r.due_at <= :endOfToday
                                """);
                q.setParameter("endOfToday", endOfToday);
                return ((Number) q.getSingleResult()).longValue();
        }

        public long countReviewedToday(OffsetDateTime startOfToday, OffsetDateTime endOfToday) {
                var q = em.createNativeQuery("""
                                select count(*) from review_log rl
                                where rl.reviewed_at >= :startOfToday and rl.reviewed_at <= :endOfToday
                                """);
                q.setParameter("startOfToday", startOfToday);
                q.setParameter("endOfToday", endOfToday);
                return ((Number) q.getSingleResult()).longValue();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> countDailySince(OffsetDateTime since) {
                var q = em.createNativeQuery("""
                                select date_trunc('day', rl.reviewed_at) as day, count(*) as cnt
                                from review_log rl
                                where rl.reviewed_at >= :since
                                group by day
                                order by day
                                """);
                q.setParameter("since", since);
                return (List<Object[]>) q.getResultList();
        }

        // === Por deck ===

        public long countCardsByDeck(UUID deckId) {
                var q = em.createNativeQuery("""
                                select count(*) from cards c where c.deck_id = :deckId
                                """);
                q.setParameter("deckId", deckId);
                return ((Number) q.getSingleResult()).longValue();
        }

        public long countDueTodayByDeck(UUID deckId, OffsetDateTime endOfToday) {
                var q = em.createNativeQuery("""
                                select count(*)
                                from reviews r
                                join cards c on c.id = r.card_id
                                where c.deck_id = :deckId and r.due_at <= :endOfToday
                                """);
                q.setParameter("deckId", deckId);
                q.setParameter("endOfToday", endOfToday);
                return ((Number) q.getSingleResult()).longValue();
        }

        public long countReviewedTodayByDeck(UUID deckId, OffsetDateTime startOfToday, OffsetDateTime endOfToday) {
                var q = em.createNativeQuery("""
                                select count(*)
                                from review_log rl
                                join cards c on c.id = rl.card_id
                                where c.deck_id = :deckId
                                  and rl.reviewed_at >= :startOfToday and rl.reviewed_at <= :endOfToday
                                """);
                q.setParameter("deckId", deckId);
                q.setParameter("startOfToday", startOfToday);
                q.setParameter("endOfToday", endOfToday);
                return ((Number) q.getSingleResult()).longValue();
        }

        @SuppressWarnings("unchecked")
        public List<Object[]> countDailySinceByDeck(UUID deckId, OffsetDateTime since) {
                var q = em.createNativeQuery("""
                                select date_trunc('day', rl.reviewed_at) as day, count(*) as cnt
                                from review_log rl
                                join cards c on c.id = rl.card_id
                                where c.deck_id = :deckId and rl.reviewed_at >= :since
                                group by day
                                order by day
                                """);
                q.setParameter("deckId", deckId);
                q.setParameter("since", since);
                return (List<Object[]>) q.getResultList();
        }
}
