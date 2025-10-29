package dev.cristianinbits.flashcards.review.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.PastOrPresent;

/**
 * Solicitud para crear o iniciar una nueva sesión de repaso (ReviewSession).
 *
 * Si {@code startedAt} no se proporciona, el servicio establecerá
 * automáticamente la fecha y hora actual en formato UTC. En caso de enviarse,
 * debe ser una fecha pasada o presente, nunca futura.
 *
 * @param startedAt instante de inicio de la sesión de repaso; debe ser pasado o
 *                  presente (opcional)
 */
public record ReviewSessionCreateRequest(
        @PastOrPresent OffsetDateTime startedAt
) { }
