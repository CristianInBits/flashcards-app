package dev.cristianinbits.flashcards.card.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Representa la solicitud para crear una nueva tarjeta (Card).
 * 
 * Este record actúa como DTO de entrada, validando los datos proporcionados
 * por el cliente antes de crear la entidad correspondiente.
 */
public record CardCreateRequest(
        @NotNull UUID deckId,
        @NotBlank @Size(max = 10000) String front,
        @NotBlank @Size(max = 10000) String back,
        @NotBlank @Size(max = 1000) String tags,
        Boolean latex) {
}
