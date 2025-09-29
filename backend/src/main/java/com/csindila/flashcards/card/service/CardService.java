package com.csindila.flashcards.card.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.csindila.flashcards.card.domain.Card;
import com.csindila.flashcards.card.dto.CardCreateRequest;
import com.csindila.flashcards.card.dto.CardDto;
import com.csindila.flashcards.card.dto.CardUpdateRequest;
import com.csindila.flashcards.card.repo.CardRepository;
import com.csindila.flashcards.deck.repo.DeckRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cards;
    private final DeckRepository decks;

    public CardDto create(CardCreateRequest req) {
        var deck = decks.findById(req.deckId()).orElseThrow(() -> new NoSuchElementException("Deck no encontrado"));
        var c = new Card();
        c.setDeck(deck);
        c.setFront(req.front());
        c.setBack(req.back());
        c.setTags(req.tags());
        if (req.latex() != null) {
            c.setLatex(req.latex());
        }
        c = cards.save(c);
        return toDto(c);
    }

    @Transactional(readOnly = true)
    public Page<CardDto> listByDeck(UUID deckId, Pageable pageable) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado");
        }
        return cards.findByDeck_Id(deckId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CardDto> search(UUID deckId, String q, String tag, Pageable pageable) {
        if (!decks.existsById(deckId)) {
            throw new NoSuchElementException("Deck no encontrado");
        }
        var normalizedQ = (q == null || q.isBlank()) ? null : q;
        var normalizedTag = (tag == null || tag.isBlank()) ? null : tag;
        return cards.search(deckId, normalizedQ, normalizedTag, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CardDto get(UUID id) {
        var c = cards.findById(id).orElseThrow(() -> new NoSuchElementException("Card no encontrada"));
        return toDto(c);
    }

    public CardDto update(UUID id, CardUpdateRequest req) {
        var c = cards.findById(id).orElseThrow(() -> new NoSuchElementException("Card no encontrada"));
        c.setFront(req.front());
        c.setBack(req.back());
        c.setTags(req.tags());
        if (req.latex() != null) {
            c.setLatex(req.latex());
        }
        return toDto(c);
    }

    public void delete(UUID id) {
        if (!cards.existsById(id))
            throw new NoSuchElementException("Card no encontrada");
        cards.deleteById(id);
    }

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
}
