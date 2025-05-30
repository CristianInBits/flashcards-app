package com.CristianInBits.flashcards.bootstrap;

import com.CristianInBits.flashcards.models.Flashcard;
import com.CristianInBits.flashcards.models.LearningStatus;
import com.CristianInBits.flashcards.repository.FlashcardRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TestDataLoader {

    private final FlashcardRepository repository;

    public TestDataLoader(FlashcardRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadTestData() {
        repository.save(new Flashcard(null, "¿Qué es una promesa en JS?",
                "Un objeto que representa una operación asíncrona", "JavaScript", LearningStatus.NO_CONOCIDA));
        repository.save(new Flashcard(null, "¿Capital de Italia?", "Roma", "Geografía", LearningStatus.MEDIO));
        repository.save(
                new Flashcard(null, "¿Quién escribió Hamlet?", "Shakespeare", "Literatura", LearningStatus.APRENDIDA));
    }
}