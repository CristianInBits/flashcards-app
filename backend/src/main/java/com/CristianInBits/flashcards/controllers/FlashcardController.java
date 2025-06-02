package com.CristianInBits.flashcards.controllers;

import com.CristianInBits.flashcards.models.Flashcard;
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
    public List<Flashcard> getFiltered(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String collection) {

        if (topic != null && collection != null) {
            return service.getByCollectionAndTopic(collection, topic);
        } else if (collection != null) {
            return service.getByCollection(collection);
        } else if (topic != null) {
            return service.getByTopic(topic);
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

    @PostMapping("/{id}/progress")
    public Flashcard updateProgress( @PathVariable Long id, @RequestParam boolean remembered) {
        return service.updateLearningProgress(id, remembered);
    }

    @DeleteMapping("/{id}")
    public Flashcard delete(@PathVariable Long id) {
        return service.delete(id)
                .orElseThrow(NoSuchElementException::new);
    }
}
