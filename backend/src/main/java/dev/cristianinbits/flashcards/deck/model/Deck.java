package dev.cristianinbits.flashcards.deck.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Representa una baraja (deck) dentro del sistema de Flashcards.
 * 
 * Esta entidad almacena la información básica de una baraja creada por el
 * usuario, * incluyendo su identificador único, nombre, descripción y la fecha
 * de creación.
 * 
 * Cada baraja puede contener múltiples tarjetas (cards), aunque la relación no
 * se define en esta clase para mantener la separación de responsabilidades.
 */
@Entity
@Table(name = "decks")
@Getter
@Setter
@NoArgsConstructor
public class Deck {

    /**
     * Identificador único de la baraja.
     * Se genera automáticamente utilizando el tipo UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Nombre asignado a la baraja.
     * Es obligatorio y tiene una longitud máxima de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descripción opcional de la baraja.
     * Se almacena en una columna de tipo texto largo.
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * Fecha y hora en la que se creó la baraja.
     * Se almacena en formato UTC y no puede modificarse una vez creada.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Asigna automáticamente la fecha de creación antes de insertar
     * el registro en la base de datos, en caso de no haberse definido previamente.
     */
    @PrePersist
    void prePersist() {
        if (createdAt == null)
            createdAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
