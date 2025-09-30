package com.csindila.flashcards.deck.service;

import com.csindila.flashcards.deck.domain.Deck;
import com.csindila.flashcards.deck.dto.*;
import com.csindila.flashcards.deck.repo.DeckRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeckService {

    private final DeckRepository repo;

    public DeckDto create(DeckCreateRequest req) {
        var d = new Deck();
        d.setName(req.name());
        d.setDescription(req.description());
        d = repo.save(d);
        return toDto(d);
    }

    @Transactional(readOnly = true)
    public Page<DeckDto> list(Pageable pageable) {
        return repo.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public DeckDto get(UUID id) {
        var d = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Deck no encontrado"));
        return toDto(d);
    }

    public DeckDto update(UUID id, DeckUpdateRequest req) {
        var d = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Deck no encontrado"));
        d.setName(req.name());
        d.setDescription(req.description());
        return toDto(d);
    }

    public void delete(UUID id) {
        if (!repo.existsById(id))
            throw new NoSuchElementException("Deck no encontrado");
        repo.deleteById(id);
    }

    private DeckDto toDto(Deck d) {
        return new DeckDto(d.getId(), d.getName(), d.getDescription(), d.getCreatedAt());
    }
}
