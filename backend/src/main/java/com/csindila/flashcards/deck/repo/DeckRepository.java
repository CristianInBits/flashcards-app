package com.csindila.flashcards.deck.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csindila.flashcards.deck.domain.Deck;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

}
