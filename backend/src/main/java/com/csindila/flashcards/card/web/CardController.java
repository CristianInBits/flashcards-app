package com.csindila.flashcards.card.web;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.csindila.flashcards.card.dto.CardCreateRequest;
import com.csindila.flashcards.card.dto.CardDto;
import com.csindila.flashcards.card.dto.CardUpdateRequest;
import com.csindila.flashcards.card.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    @PostMapping("/cards")
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardCreateRequest req) {
        var created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/decks/{deckId}/cards")
    public Page<CardDto> listByDeck(
            @PathVariable UUID deckId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        // sort="campo,asc|desc"
        String[] parts = sort.split(",", 2);
        String property = parts[0];
        String dir = parts.length > 1 ? parts[1] : "asc";

        // whitelist de propiedades ordenables
        Set<String> allowed = Set.of("createdAt", "name");
        if (!allowed.contains(property)) {
            throw new IllegalArgumentException("Campo de orden inválido: " + property);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(direction, property)));
        return service.listByDeck(deckId, pageable);
    }

    @GetMapping("/decks/{deckId}/cards/search")
    public Page<CardDto> search(
            @PathVariable UUID deckId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        var pageable = PageRequest.of(page, size, Sort.by(sort.split(",")));
        return service.search(deckId, q, tag, pageable);
    }

    @GetMapping("/cards/{id}")
    public CardDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/cards/{id}")
    public CardDto update(@PathVariable UUID id, @Valid @RequestBody CardUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
