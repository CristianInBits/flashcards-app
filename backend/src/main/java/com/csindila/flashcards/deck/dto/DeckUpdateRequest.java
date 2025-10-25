package com.csindila.flashcards.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
DTO utilizado para actualizar los datos de un mazo existente.
Se usa al recibir una solicitud HTTP PUT o PATCH.
Incluye validaciones similares a las de creación para garantizar la consistencia de los datos.
*/
public record DeckUpdateRequest(
        /*
        Nuevo nombre del mazo. No puede estar vacío y tiene una longitud máxima de 100 caracteres.
        */
        @NotBlank @Size(max = 100) String name,

        /*
        Nueva descripción del mazo. Longitud máxima de 10 000 caracteres.
        */
        @Size(max = 10000) String description) {
}
