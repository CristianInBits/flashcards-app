package com.CristianInBits.flashcards.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "error", "Flashcard no encontrada",
                        "timestamp", LocalDateTime.now(),
                        "status", 404),
                HttpStatus.NOT_FOUND);
    }

    // Puedes añadir más handlers para otras excepciones
}
