package com.csindila.flashcards.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
Objeto de transferencia de datos (DTO) utilizado para crear un nuevo mazo (Deck).
Se usa al recibir una solicitud HTTP POST con los datos del mazo que el usuario desea crear.
Incluye validaciones para garantizar que los campos cumplan las restricciones necesarias.
*/
public record DeckCreateRequest(
        /*
        Nombre del mazo. No puede estar vacío y tiene una longitud máxima de 100 caracteres.
        */
        @NotBlank @Size(max = 100) String name,

        /*
        Descripción opcional del mazo. Longitud máxima de 10 000 caracteres.
        */
        @Size(max = 10000) String description) {
}