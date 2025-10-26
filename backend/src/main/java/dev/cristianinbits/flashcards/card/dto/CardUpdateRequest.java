package dev.cristianinbits.flashcards.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representa la solicitud para actualizar una tarjeta existente.
 * 
 * Este record se utiliza como DTO de entrada para las operaciones de actualización,
 * validando los datos antes de aplicar los cambios en la base de datos.
 */
public record CardUpdateRequest(
        @NotBlank @Size(max = 10000) String front,
        @NotBlank @Size(max = 10000) String back,
        @Size(max = 1000) String tags,
        Boolean latex) {
}