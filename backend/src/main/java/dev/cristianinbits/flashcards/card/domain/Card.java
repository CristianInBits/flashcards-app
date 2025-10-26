package dev.cristianinbits.flashcards.card.domain;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import dev.cristianinbits.flashcards.deck.domain.Deck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa una tarjeta (Card) dentro de un mazo en el sistema de Flashcards.
 * 
 * Cada tarjeta pertenece obligatoriamente a un mazo y contiene un anverso
 * (front), un reverso (back), etiquetas opcionales y metadatos como las fechas
 * de creación y actualización. También puede estar marcada como compatible con
 * LaTeX para permitir renderizado matemático o de fórmulas.
 * 
 * La relación con {@link Deck} es de muchos a uno (varias tarjetas pueden
 * pertenecer a un mismo mazo). Los eventos de persistencia establecen las
 * marcas de tiempo automáticamente.
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    /** Identificador único de la tarjeta. Se genera automáticamente como UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Mazo al que pertenece esta tarjeta.
     * La relación es obligatoria y se carga de forma diferida (lazy loading).
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    /** Texto mostrado en el anverso de la tarjeta. */
    @Column(nullable = false)
    private String front;

    /** Texto mostrado en el reverso de la tarjeta. */
    @Column(nullable = false)
    private String back;

    /** Etiquetas asociadas a la tarjeta, separadas por comas (opcional). */
    @Column
    private String tags;

    /** Indica si la tarjeta contiene contenido en formato LaTeX. */
    @Column(nullable = false)
    private boolean latex = false;

    /** Fecha y hora de creación de la tarjeta, almacenada en formato UTC. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la tarjeta, almacenada en formato
     * UTC.
     */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Asigna las marcas de tiempo de creación y actualización antes de insertar
     * el registro en la base de datos.
     */
    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        if (createdAt == null)
            createdAt = now;
        updatedAt = now;
    }

    /**
     * Actualiza la marca de tiempo de modificación antes de realizar una
     * actualización.
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
