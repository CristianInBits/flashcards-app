package dev.cristianinbits.flashcards.shared.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;

/**
 * Manejador global de excepciones para la API REST.
 * 
 * Captura excepciones comunes lanzadas en el backend y devuelve
 * respuestas estructuradas con códigos de estado HTTP apropiados.
 * 
 * Anotado con {@code @RestControllerAdvice} para que Spring lo aplique
 * a todos los controladores del contexto.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

  /**
   * Maneja las excepciones {@link IllegalArgumentException} generadas por
   * solicitudes inválidas.
   *
   * @param ex excepción lanzada
   * @return mapa con la clave de error y el mensaje descriptivo
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException ex) {
    return Map.of("error", "bad_request", "message", ex.getMessage());
  }

  /**
   * Maneja las excepciones {@link NoSuchElementException} cuando no se encuentra
   * un recurso.
   *
   * @param ex excepción lanzada
   * @return mapa con la clave de error y el mensaje descriptivo
   */
  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> notFound(NoSuchElementException ex) {
    return Map.of("error", "not_found", "message", ex.getMessage());
  }
}
