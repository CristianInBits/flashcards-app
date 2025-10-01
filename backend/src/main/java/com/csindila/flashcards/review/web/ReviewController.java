package com.csindila.flashcards.review.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.csindila.flashcards.review.dto.ReviewAnswerRequest;
import com.csindila.flashcards.review.dto.ReviewDto;
import com.csindila.flashcards.review.dto.ReviewQueueItemDto;
import com.csindila.flashcards.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    /**
     * Cola de estudio: tarjetas vencidas (due <= ahora).
     */
    @GetMapping("/due")
    public List<ReviewQueueItemDto> due(@RequestParam(defaultValue = "20") int limit) {
        return service.getDueQueue(limit);
    }

    @GetMapping("/next")
    public ResponseEntity<ReviewQueueItemDto> next(
            @RequestParam(required = false) UUID deckId,
            @RequestParam(defaultValue = "false") boolean shuffle) {
        var item = service.getNext(deckId, shuffle);
        if (item == null)
            return ResponseEntity.noContent().build(); // 204 si no hay vencidas
        return ResponseEntity.ok(item);
    }

    /**
     * Responder una tarjeta: recalcula SRS.
     */
    @PostMapping("/{cardId}/answer")
    public ResponseEntity<ReviewDto> answer(
            @PathVariable UUID cardId,
            @Valid @RequestBody ReviewAnswerRequest req) {
        var updated = service.answer(cardId, req.grade());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{cardId}/skip")
    public ReviewDto skip(
            @PathVariable UUID cardId,
            @RequestParam(defaultValue = "10") int minutes) {
        return service.skip(cardId, minutes);
    }

    /**
     * Consultar estado SRS de una tarjeta.
     */
    @GetMapping("/{cardId}")
    public ReviewDto get(@PathVariable UUID cardId) {
        return service.get(cardId);
    }
}