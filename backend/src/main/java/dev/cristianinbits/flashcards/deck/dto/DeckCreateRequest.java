package dev.cristianinbits.flashcards.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa la solicitud para crear una nueva baraja.
 * 
 * Este record se utiliza como DTO de entrada en las operaciones de creación,
 * validando los datos antes de persistirlos en la base de datos.
 */
public record DeckCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 10000) String description
) { }