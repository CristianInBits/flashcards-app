package com.CristianInBits.flashcards.services;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;
import com.CristianInBits.flashcards.repository.FlashcardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FlashcardService {

    private final FlashcardRepository repository;

    public FlashcardService(FlashcardRepository repository) {
        this.repository = repository;
    }

    public Flashcard getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Flashcard con id " + id + " no encontrada"));
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

    public List<Flashcard> getByTopicAndStatus(String topic, LearningStatus status) {
        return repository.findByTopicAndStatus(topic, status);
    }

    public List<Flashcard> getByCollection(String collection) {
        return repository.findByCollection(collection);
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

    public Optional<Flashcard> delete(Long id) {
        return repository.findById(id).map(card -> {
            repository.deleteById(id);
            return card;
        });
    }

    public Flashcard updateLearningProgress(Long id, boolean remembered) {
        Flashcard card = repository.findById(id).orElseThrow();
        int[] intervals = { 0, 1, 3, 7, 30 };

        if (remembered) {
            card.setLevel(Math.min(card.getLevel() + 1, intervals.length - 1));
        } else {
            card.setLevel(0);
        }

        LocalDate nextReview = LocalDate.now().plusDays(intervals[card.getLevel()]);
        card.setNextReviewDate(nextReview);
        return repository.save(card);
    }

}
