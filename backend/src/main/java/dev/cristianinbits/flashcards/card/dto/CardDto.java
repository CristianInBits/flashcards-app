package dev.cristianinbits.flashcards.card.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa los datos transferidos de una tarjeta (Card) hacia el cliente.
 * 
 * Este DTO de salida contiene los campos relevantes que describen una tarjeta,
 * incluyendo los identificadores, contenido, etiquetas, compatibilidad con LaTeX
 * y marcas temporales de creación y actualización.
 */
public record CardDto(
        UUID id,
        UUID deckId,
        String front,
        String back,
        String tags,
        boolean latex,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
