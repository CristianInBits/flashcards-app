package dev.cristianinbits.flashcards.review.dto;

import java.time.OffsetDateTime;

/**
 * Solicitud para crear o iniciar una nueva sesión de repaso (ReviewSession).
 *
 * Si {@code startedAt} no se proporciona, el servicio establecerá
 * automáticamente la fecha y hora actual en formato UTC.
 *
 * @param startedAt instante de inicio de la sesión de repaso (opcional)
 */
public record ReviewSessionCreateRequest(
        OffsetDateTime startedAt
) { }
