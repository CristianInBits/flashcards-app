package com.CristianInBits.flashcards.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class FlashcardTestController {

    @GetMapping()
    public List<Map<String, String>> getSampleFlashcards() {

        return List.of(
                Map.of(
                        "question", "¿Cuál es la capital de Francia?",
                        "answer", "París",
                        "topic", "Geografía"),
                Map.of(
                        "question", "¿Cuánto es 5 x 6?",
                        "answer", "30",
                        "topic", "Matemáticas"));
    }
}
