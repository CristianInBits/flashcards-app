package dev.cristianinbits.flashcards.deck.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Representa los datos transferidos de una baraja (Deck) hacia el cliente.
 * 
 * Este record actúa como DTO de salida y contiene los campos visibles o relevantes
 * que el cliente necesita conocer, ocultando detalles internos de la entidad.
 */
public record DeckDto(
        UUID id,
        String name,
        String description,
        OffsetDateTime createdAt
) { }