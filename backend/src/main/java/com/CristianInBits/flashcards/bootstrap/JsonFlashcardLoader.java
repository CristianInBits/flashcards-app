package com.CristianInBits.flashcards.bootstrap;

import com.CristianInBits.flashcards.repository.FlashcardRepository;
import com.CristianInBits.flashcards.models.Flashcard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class JsonFlashcardLoader {

    private final FlashcardRepository repository;
    private final ObjectMapper mapper;

    public JsonFlashcardLoader(FlashcardRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @PostConstruct
    public void loadFromJson() throws Exception {
        if (repository.count() == 0) {
            //InputStream inputStream = new ClassPathResource("data/flashcards.json").getInputStream();
            InputStream inputStream = new ClassPathResource("data/flashcardsBBDD.json").getInputStream();
            List<Flashcard> flashcards = mapper.readValue(inputStream, new TypeReference<>() {
            });
            repository.saveAll(flashcards);
            System.out.println("✅ Flashcards cargadas desde JSON");
        }
    }
}
