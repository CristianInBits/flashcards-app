package dev.cristianinbits.flashcards.review.domain;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import dev.cristianinbits.flashcards.deck.domain.Deck;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

/**
 * Representa una sesión de repaso (ReviewSession) dentro del sistema de
 * Flashcards.
 *
 * Una sesión de repaso agrupa un conjunto de eventos de revisión realizados por
 * el usuario sobre un mazo específico. Permite registrar información analítica
 * como el número total de tarjetas repasadas, los aciertos, los fallos y la
 * duración total de la sesión.
 *
 * Esta entidad es opcional en el flujo de estudio: puede existir para análisis
 * de uso y métricas de usuario, pero no interviene en la planificación del
 * algoritmo de repetición espaciada.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReviewSession {

    /**
     * Identificador único de la sesión de repaso. Se genera automáticamente
     * mediante UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Mazo al que pertenece la sesión de repaso.
     * La relación es obligatoria y se carga de forma diferida.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deck_id", nullable = false)
    @ToString.Exclude
    private Deck deck;

    /**
     * Fecha y hora en la que se inició la sesión. Se establece automáticamente si
     * no se define.
     */
    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    /**
     * Fecha y hora en la que finalizó la sesión (puede ser nula si la sesión sigue
     * en curso).
     */
    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    /** Número total de tarjetas repasadas durante la sesión. */
    @Column(name = "total_cards", nullable = false)
    private int totalCards = 0;

    /** Número de respuestas correctas registradas en la sesión. */
    @Column(name = "correct", nullable = false)
    private int correct = 0;

    /** Número de respuestas incorrectas registradas en la sesión. */
    @Column(name = "incorrect", nullable = false)
    private int incorrect = 0;

    /** Duración total de la sesión en segundos. */
    @Column(name = "duration_sec", nullable = false)
    private int durationSec = 0;

    /**
     * Establece automáticamente la marca temporal de inicio antes de insertar
     * el registro en la base de datos si no se ha definido explícitamente.
     */
    @PrePersist
    void prePersist() {
        if (startedAt == null) {
            startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }
}
