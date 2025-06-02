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
            String[] files = {
                    "data/flashcardsBBDD.json",
                    "data/flashcardsASO.json",
                    "data/flashcardsPC.json"
            };

            for (String file : files) {
                InputStream input = new ClassPathResource(file).getInputStream();
                List<Flashcard> cards = mapper.readValue(input, new TypeReference<>() {
                });
                repository.saveAll(cards);
                System.out.println("Cargadas desde: " + file + " (" + cards.size() + " tarjetas)");
            }
        } else {
            System.out.println("Ya existen tarjetas en la base de datos, no se cargan desde JSON.");
        }
    }

}
