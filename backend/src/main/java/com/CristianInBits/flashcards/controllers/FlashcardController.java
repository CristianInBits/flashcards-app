package com.CristianInBits.flashcards.controllers;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;
import com.CristianInBits.flashcards.services.FlashcardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin
public class FlashcardController {

    private final FlashcardService service;

    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    @GetMapping
    public List<Flashcard> getAll(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) LearningStatus status) {
        if (topic != null)
            return service.getByTopic(topic);
        if (status != null)
            return service.getByStatus(status);
        return service.getAll();
    }

    @PostMapping
    public Flashcard create(@RequestBody Flashcard flashcard) {
        return service.save(flashcard);
    }

    @PostMapping("/{id}/status")
    public Flashcard updateStatus(@PathVariable Long id, @RequestParam LearningStatus status) {
        return service.updateStatus(id, status)
                .orElseThrow(() -> new RuntimeException("Flashcard no encontrada"));
    }
}
