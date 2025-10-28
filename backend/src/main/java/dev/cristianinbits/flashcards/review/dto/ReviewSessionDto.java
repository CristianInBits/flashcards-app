package dev.cristianinbits.flashcards.review.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa una sesión de repaso (ReviewSession) utilizada en las respuestas
 * de la API.
 *
 * Contiene los datos principales de una sesión de estudio, incluyendo el mazo
 * asociado, las marcas de tiempo, métricas de rendimiento y duración total. Puede
 * reflejar tanto sesiones finalizadas como en curso.
 *
 * @param id          identificador único de la sesión
 * @param deckId      identificador del mazo asociado
 * @param startedAt   instante en que comenzó la sesión
 * @param endedAt     instante en que finalizó la sesión (puede ser null si
 *                    sigue en curso)
 * @param totalCards  número total de tarjetas repasadas
 * @param correct     número de respuestas correctas
 * @param incorrect   número de respuestas incorrectas
 * @param durationSec duración total de la sesión, en segundos
 */
public record ReviewSessionDto(
        UUID id,
        UUID deckId,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt,
        int totalCards,
        int correct,
        int incorrect,
        int durationSec
) { }
