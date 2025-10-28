package dev.cristianinbits.flashcards.review.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import dev.cristianinbits.flashcards.card.domain.Card;

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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa un evento de revisión individual (ReviewEvent) dentro del sistema
 * de Flashcards.
 *
 * Cada evento corresponde a un intento de respuesta sobre una tarjeta
 * específica, registrando el resultado (acierto o fallo), el tiempo empleado y
 * el momento en que se respondió.
 * 
 * Puede asociarse opcionalmente a una {@link ReviewSession} (cuando forma parte
 * de una tanda) o existir de manera independiente. Además, almacena snapshots
 * del estado anterior y nuevo de los parámetros del algoritmo SRS, útiles para
 * auditoría o análisis histórico.
 *
 * Los snapshots permiten reconstruir el progreso de una tarjeta en el tiempo o
 * recalcular el comportamiento del algoritmo si se ajustan los parámetros en el
 * futuro.
 */
@Entity
@Table(name = "review_events")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReviewEvent {

    /**
     * Identificador único del evento de revisión. Se genera automáticamente
     * mediante UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Sesión de repaso a la que pertenece este evento.
     * 
     * Es una relación opcional (ON DELETE SET NULL), ya que puede registrarse un
     * evento sin estar asociado a una sesión concreta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "review_id")
    @ToString.Exclude
    private ReviewSession review;

    /**
     * Tarjeta sobre la que se realizó el intento de repaso.
     * Esta relación es obligatoria y se carga de forma diferida.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    @ToString.Exclude
    private Card card;

    /** Fecha y hora en la que se respondió la tarjeta (en formato UTC). */
    @Column(name = "answered_at", nullable = false)
    private OffsetDateTime answeredAt;

    /**
     * Resultado del intento.
     * 
     * 0 indica fallo, 1 indica acierto.
     * Se deja margen para posibles escalas futuras (por ejemplo, 0–5).
     */
    @Column(name = "result", nullable = false)
    @Min(0)
    @Max(1)
    private short result;

    /** Tiempo total empleado en responder, expresado en milisegundos. */
    @Column(name = "elapsed_ms", nullable = false)
    @Min(0)
    private int elapsedMs = 0;

    // --- Snapshots opcionales (auditoría / analítica) ---

    /** Fecha previa en la que la tarjeta estaba programada para revisión. */
    @Column(name = "prev_due_at")
    private OffsetDateTime prevDueAt;

    /** Nueva fecha programada de revisión tras este evento. */
    @Column(name = "new_due_at")
    private OffsetDateTime newDueAt;

    /** Intervalo previo (en días) antes de este intento de repaso. */
    @Column(name = "prev_interval")
    @Min(0)
    private Integer prevInterval;

    /** Nuevo intervalo (en días) asignado después del intento de repaso. */
    @Column(name = "new_interval")
    @Min(0)
    private Integer newInterval;

    /** Factor de facilidad previo del algoritmo SRS antes del intento. */
    @Column(name = "prev_ease", precision = 4, scale = 2)
    @DecimalMin(value = "1.30")
    @DecimalMax(value = "3.00")
    private BigDecimal prevEase;

    /** Nuevo factor de facilidad calculado tras el intento. */
    @Column(name = "new_ease", precision = 4, scale = 2)
    @DecimalMin(value = "1.30")
    @DecimalMax(value = "3.00")
    private BigDecimal newEase;

    /**
     * Establece automáticamente la marca temporal de respuesta antes de insertar
     * el registro en la base de datos si no se ha definido previamente.
     */
    @PrePersist
    void prePersist() {
        if (answeredAt == null) {
            answeredAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }
}
