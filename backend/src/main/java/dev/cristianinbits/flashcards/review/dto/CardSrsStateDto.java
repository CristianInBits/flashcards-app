package dev.cristianinbits.flashcards.review.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa el snapshot actual del estado SRS de una tarjeta.
 *
 * Este DTO expone la información mínima necesaria para diagnóstico, estadísticas
 * o depuración del planificador (no suele ser imprescindible en los flujos de UI).
 *
 * Definiciones:
 * - intervalDays: número de días del intervalo vigente hasta la próxima revisión.
 * - easeFactor: factor de facilidad usado por el algoritmo (rango típico 1.30–3.00).
 * - repetitions: número total de repeticiones acumuladas de la tarjeta (no es una racha).
 * - lastResult: último resultado registrado (0 = fallo, 1 = acierto; escalable a 0–5 en el futuro).
 *
 * @param cardId identificador de la tarjeta (coincide con la PK del estado)
 * @param dueAt próxima fecha programada de repaso
 * @param intervalDays intervalo actual en días
 * @param easeFactor factor de facilidad del algoritmo SRS
 * @param repetitions total de repeticiones acumuladas
 * @param lastResult último resultado registrado (0 o 1)
 * @param updatedAt instante de última actualización del estado
 */
public record CardSrsStateDto(
        UUID cardId,
        OffsetDateTime dueAt,
        int intervalDays,
        BigDecimal easeFactor,
        int repetitions,
        short lastResult,
        OffsetDateTime updatedAt
) { }

