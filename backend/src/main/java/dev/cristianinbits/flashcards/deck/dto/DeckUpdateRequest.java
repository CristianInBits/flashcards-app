package dev.cristianinbits.flashcards.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa la solicitud para actualizar una baraja existente.
 * 
 * Este record se utiliza como DTO de entrada en las operaciones de actualización,
 * validando los datos enviados por el cliente antes de aplicar los cambios en la entidad Deck.
 */
public record DeckUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 10000) String description
) { }