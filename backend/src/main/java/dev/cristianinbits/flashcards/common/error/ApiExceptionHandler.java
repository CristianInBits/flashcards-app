package dev.cristianinbits.flashcards.common.error;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

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

  /**
   * Maneja las excepciones {@link MethodArgumentNotValidException} producidas
   * cuando la validación de un objeto recibido en el cuerpo de la solicitud
   * falla.
   *
   * Devuelve un listado con los campos inválidos y los mensajes de error
   * asociados.
   *
   * @param ex excepción lanzada por el mecanismo de validación de Spring
   * @return mapa con el tipo de error y una lista detallada de los campos
   *         afectados
   */
  @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
        .toList();
    return Map.of("error", "validation_error", "details", errors);
  }

  /**
   * Maneja las excepciones{@link jakarta.validation.ConstraintViolationException}
   * que se generan al violar restricciones de validación en parámetros de métodos
   * o propiedades individuales.
   *
   * Devuelve un listado con las violaciones detectadas, indicando el campo y el
   * mensaje.
   *
   * @param ex excepción lanzada por el validador de Bean Validation
   * @return mapa con el tipo de error y los detalles de las violaciones de
   *         validación
   */

  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
    var errors = ex.getConstraintViolations().stream()
        .map(cv -> Map.of("field", cv.getPropertyPath().toString(), "message", cv.getMessage()))
        .toList();
    return Map.of("error", "validation_error", "details", errors);
  }

  /**
   * Maneja las excepciones {@link HttpMessageNotReadableException} que ocurren
   * cuando el cuerpo de la solicitud no puede ser leído o convertido
   * correctamente, normalmente debido a un JSON mal formado o a un tipo de dato
   * inválido.
   *
   * @param ex excepción lanzada durante el proceso de deserialización de la
   *           solicitud
   * @return mapa con el tipo de error y un mensaje descriptivo del problema de
   *         lectura
   */
  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> handleBadJson(HttpMessageNotReadableException ex) {
    return Map.of("error", "bad_request", "message", "JSON mal formado o tipo de dato inválido");
  }

  /**
   * Maneja las excepciones {@link IllegalStateException} generadas cuando
   * se intenta realizar una operación que entra en conflicto con el estado
   * actual del recurso.
   *
   * Devuelve una respuesta con el código HTTP 409 (Conflict), incluyendo
   * detalles del error y el mensaje explicativo.
   *
   * @param ex excepción lanzada que describe el conflicto de estado
   * @return mapa con el código de estado, tipo de error y mensaje asociado
   */
  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleIllegalState(IllegalStateException ex) {
    return Map.of(
        "status", HttpStatus.CONFLICT.value(),
        "error", "Conflict",
        "message", ex.getMessage());
  }

}
