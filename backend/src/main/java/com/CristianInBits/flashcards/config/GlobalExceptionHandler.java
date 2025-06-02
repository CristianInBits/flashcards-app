package com.CristianInBits.flashcards.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 * 
 * Esta clase captura excepciones comunes y transforma las respuestas
 * de error en un formato uniforme y legible para el cliente frontend.
 * 
 * Se utiliza `@ControllerAdvice` para aplicar esta lógica a todos los
 * controladores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentra una entidad en la base de datos.
     * 
     * Captura {@link NoSuchElementException} y devuelve una respuesta HTTP 404
     * con información adicional sobre el error.
     *
     * @param ex Excepción lanzada desde el servicio o repositorio.
     * @return Respuesta con cuerpo JSON y estado HTTP 404.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "error", "Flashcard no encontrada",
                        "timestamp", LocalDateTime.now(),
                        "status", 404),
                HttpStatus.NOT_FOUND);
    }

    // Puedes añadir más @ExceptionHandler aquí
}
