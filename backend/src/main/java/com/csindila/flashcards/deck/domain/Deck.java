package com.csindila.flashcards.deck.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/*
Clase que representa la entidad Deck (mazo de tarjetas).
Cada Deck agrupa un conjunto de tarjetas relacionadas y contiene información básica como su nombre, descripción y fecha de creación.
Se mapea a la tabla "decks" en la base de datos.
*/

@Entity
@Table(name = "decks")
@Getter
@Setter
public class Deck {

    /*
     * Identificador único del mazo.
     * Se genera automáticamente mediante UUID al persistirse en la base de datos.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /*
     * Nombre del mazo. Campo obligatorio con longitud máxima de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /*
     * Descripción opcional del mazo. Se almacena como tipo de dato "text" en la
     * base de datos.
     */
    @Column(columnDefinition = "text")
    private String description;

    /*
     * Fecha y hora de creación del mazo en formato UTC.
     * No puede ser nula ni modificarse una vez creada la entidad.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /*
     * Método ejecutado automáticamente antes de insertar el registro en la base
     * de datos.
     * Si la fecha de creación no ha sido establecida manualmente, se asigna el
     * momento actual en UTC.
     */
    @PrePersist
    void prePersist() {
        if (createdAt == null)
            createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}