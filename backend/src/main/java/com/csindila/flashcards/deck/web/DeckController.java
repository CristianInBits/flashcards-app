package com.csindila.flashcards.deck.web;

import com.csindila.flashcards.deck.dto.*;
import com.csindila.flashcards.deck.service.DeckService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;

import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService service;

    @PostMapping
    public ResponseEntity<DeckDto> create(@Valid @RequestBody DeckCreateRequest req) {
        var created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<DeckDto> list(
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
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    public DeckDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public DeckDto update(@PathVariable UUID id, @Valid @RequestBody DeckUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
