package com.csindila.flashcards.card.web;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class CardController {

    private static final Set<String> ALLOWED_SORTS = Set.of("createdAt", "updatedAt", "front");

    private final CardService service;

    @PostMapping("/cards")
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardCreateRequest req) {
        var created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/decks/{deckId}/cards")
    public Page<CardDto> listByDeck(
            @PathVariable UUID deckId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = buildPageable(page, size, sort);
        return service.listByDeck(deckId, pageable);
    }

    @GetMapping("/decks/{deckId}/cards/search")
    public Page<CardDto> search(
            @PathVariable UUID deckId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Pageable pageable = buildPageable(page, size, sort);
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

    private Pageable buildPageable(int page, int size, String sort) {

        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        String dir = parts.length > 1 ? parts[1].trim() : "asc";

        if (!ALLOWED_SORTS.contains(property)) {
            throw new IllegalArgumentException("Campo de orden inválido: " + property);
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(new Sort.Order(direction, property)));
    }
}
