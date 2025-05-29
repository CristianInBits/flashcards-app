package com.CristianInBits.flashcards.services;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;
import com.CristianInBits.flashcards.repository.FlashcardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlashcardService {

    private final FlashcardRepository repository;

    public FlashcardService(FlashcardRepository repository) {
        this.repository = repository;
    }

    public List<Flashcard> getAll() {
        return repository.findAll();
    }

    public List<Flashcard> getByTopic(String topic) {
        return repository.findByTopic(topic);
    }

    public List<Flashcard> getByStatus(LearningStatus status) {
        return repository.findByStatus(status);
    }

    public Flashcard save(Flashcard flashcard) {
        return repository.save(flashcard);
    }

    public Optional<Flashcard> updateStatus(Long id, LearningStatus status) {
        return repository.findById(id).map(card -> {
            card.setStatus(status);
            return repository.save(card);
        });
    }
}
