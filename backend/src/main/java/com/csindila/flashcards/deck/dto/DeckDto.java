package com.csindila.flashcards.deck.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
DTO utilizado para transferir información completa de un mazo (Deck) al cliente.
Se usa como respuesta al consultar o listar mazos.
Contiene todos los campos relevantes del mazo almacenado en la base de datos.
*/
public record DeckDto(
        /*
        Identificador único del mazo.
        */
        UUID id,

        /*
        Nombre del mazo.
        */
        String name,

        /*
        Descripción del mazo.
        */
        String description,

        /*
        Fecha de creación del mazo en formato UTC.
        */
        OffsetDateTime createdAt) {
}