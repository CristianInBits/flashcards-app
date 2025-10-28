package dev.cristianinbits.flashcards.review.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa un evento de repaso (ReviewEvent) utilizado en las respuestas de
 * la API.
 * 
 * Contiene la información principal del intento de repaso de una tarjeta,
 * incluyendo el resultado, el tiempo empleado y las marcas temporales. Además,
 * incorpora snapshots del estado SRS antes y después del evento, lo que permite
 * realizar auditorías o análisis detallados de la evolución del algoritmo.
 *
 * @param id           identificador único del evento
 * @param reviewId     identificador de la sesión de repaso (puede ser null)
 * @param cardId       identificador de la tarjeta sobre la que se realizó el
 *                     intento
 * @param answeredAt   instante en el que se registró la respuesta
 * @param result       resultado del intento (0 = fallo, 1 = acierto)
 * @param elapsedMs    tiempo total invertido en responder, en milisegundos
 * @param prevDueAt    fecha programada previa de revisión (puede ser null si no
 *                     aplica)
 * @param newDueAt     nueva fecha programada de revisión tras aplicar el
 *                     algoritmo SRS
 * @param prevInterval intervalo anterior en días (puede ser null)
 * @param newInterval  nuevo intervalo en días (puede ser null)
 * @param prevEase     factor de facilidad anterior (puede ser null)
 * @param newEase      factor de facilidad nuevo calculado tras el evento (puede
 *                     ser null)
 */
public record ReviewEventDto(
        UUID id,
        UUID reviewId,
        UUID cardId,
        OffsetDateTime answeredAt,
        short result,
        int elapsedMs,
        OffsetDateTime prevDueAt,
        OffsetDateTime newDueAt,
        Integer prevInterval,
        Integer newInterval,
        BigDecimal prevEase,
        BigDecimal newEase
) { }
