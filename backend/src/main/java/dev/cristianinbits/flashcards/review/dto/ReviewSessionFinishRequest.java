package dev.cristianinbits.flashcards.review.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;

/**
 * Solicitud para finalizar una sesión de repaso (ReviewSession).
 *
 * Si {@code endedAt} no se proporciona, el servicio establecerá automáticamente
 * la fecha y hora actual en formato UTC. En caso de enviarse, debe ser una
 * fecha pasada o presente. Los contadores deben ser valores no negativos y
 * coherentes entre sí; el servicio puede realizar verificaciones de
 * consistencia.
 *
 * @param endedAt     instante de finalización de la sesión; debe ser pasado o
 *                    presente (opcional)
 * @param totalCards  número total de tarjetas repasadas (≥ 0)
 * @param correct     número total de respuestas correctas (≥ 0)
 * @param incorrect   número total de respuestas incorrectas (≥ 0)
 * @param durationSec duración total de la sesión en segundos (≥ 0)
 */
public record ReviewSessionFinishRequest(
        @PastOrPresent OffsetDateTime endedAt,
        @Min(0) int totalCards,
        @Min(0) int correct,
        @Min(0) int incorrect,
        @Min(0) int durationSec
) { }
