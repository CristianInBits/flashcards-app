package dev.cristianinbits.flashcards.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

/**
 * Solicitud para registrar un evento de repaso (ReviewEvent) asociado a una
 * tarjeta.
 *
 * Puede utilizarse en dos estilos de endpoint:
 *
 * - Con sesión: {@code POST /api/reviews/{reviewId}/events}
 * El identificador de sesión se obtiene de la ruta, por lo que el campo
 * {@code reviewId} en el cuerpo puede omitirse.
 *
 * - Sin sesión: {@code POST /api/cards/{cardId}/review}
 * En este caso, el identificador de la tarjeta se proporciona en la ruta o en
 * el cuerpo, y el campo {@code reviewId} es opcional.
 *
 * Cada evento representa un intento de respuesta del usuario sobre una tarjeta,
 * registrando el resultado (acierto o fallo) y el tiempo empleado en responder.
 *
 * @param reviewId  identificador de la sesión de repaso (opcional)
 * @param cardId    identificador de la tarjeta sobre la que se registra el
 *                  evento;
 *                  puede omitirse si se envía en la ruta del endpoint
 * @param result    resultado del intento (0 = fallo, 1 = acierto)
 * @param elapsedMs tiempo invertido en responder, expresado en milisegundos
 *                  (valor ≥ 0)
 */
public record ReviewEventCreateRequest(
                UUID reviewId,
                UUID cardId,
                @Min(0) @Max(1) short result,
                @Min(0) int elapsedMs
) { }
