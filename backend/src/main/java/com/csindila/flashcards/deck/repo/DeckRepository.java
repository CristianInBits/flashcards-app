package com.csindila.flashcards.deck.repo;

import com.csindila.flashcards.deck.domain.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DeckRepository extends JpaRepository<Deck, UUID> {
}