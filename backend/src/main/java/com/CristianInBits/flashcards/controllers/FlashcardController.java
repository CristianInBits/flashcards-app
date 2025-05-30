package com.CristianInBits.flashcards.controllers;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;
import com.CristianInBits.flashcards.services.FlashcardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin
public class FlashcardController {

    private final FlashcardService service;

    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    @GetMapping("/")
    public List<Flashcard> getAll(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) LearningStatus status) {
        if (topic != null && status != null) {
            return service.getByTopicAndStatus(topic, status);
        } else if (topic != null) {
            return service.getByTopic(topic);
        } else if (status != null) {
            return service.getByStatus(status);
        } else {
            return service.getAll();
        }
    }

    @GetMapping("/{id}")
    public Flashcard getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/")
    public Flashcard create(@RequestBody Flashcard flashcard) {
        return service.save(flashcard);
    }

    @PostMapping("/{id}/status")
    public Flashcard updateStatus(@PathVariable Long id, @RequestParam LearningStatus status) {
        return service.updateStatus(id, status)
                .orElseThrow(NoSuchElementException::new);
    }

    @PostMapping("/{id}/progress")
    public Flashcard updateProgress(
            @PathVariable Long id,
            @RequestParam boolean remembered) {
        return service.updateLearningProgress(id, remembered);
    }

    @DeleteMapping("/{id}")
    public Flashcard delete(@PathVariable Long id) {
        return service.delete(id)
                .orElseThrow(NoSuchElementException::new);
    }
}
