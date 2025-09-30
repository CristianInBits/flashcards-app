package com.csindila.flashcards.card.repo;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.csindila.flashcards.card.domain.Card;

public interface CardRepository extends JpaRepository<Card, UUID> {

  Page<Card> findByDeck_Id(UUID deckId, Pageable pageable);

  @Query("""
      SELECT c FROM Card c
      WHERE c.deck.id = :deckId
        AND (:qPattern IS NULL OR LOWER(c.front) LIKE :qPattern OR LOWER(c.back) LIKE :qPattern)
        AND (:tagPattern IS NULL OR (c.tags IS NOT NULL AND LOWER(c.tags) LIKE :tagPattern))
      """)
  Page<Card> search(
      @Param("deckId") UUID deckId,
      @Param("qPattern") String qPattern,
      @Param("tagPattern") String tagPattern,
      Pageable pageable);
}