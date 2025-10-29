package dev.cristianinbits.flashcards.review.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import dev.cristianinbits.flashcards.card.domain.Card;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
 * Representa el estado actual de una tarjeta (Card) dentro del sistema de
 * repetición espaciada (SRS).
 *
 * Esta entidad almacena el snapshot más reciente del progreso de una tarjeta,
 * incluyendo su fecha programada de próxima revisión, el intervalo en días, el
 * factor de facilidad, el número de repeticiones acumuladas y el último
 * resultado obtenido.
 *
 * A diferencia de {@link ReviewEvent}, que registra el historial completo de
 * intentos, esta clase mantiene un único registro por tarjeta, lo que permite
 * determinar rápidamente qué tarjetas están pendientes de repaso mediante una
 * simple consulta (por ejemplo, {@code WHERE due_at <= NOW()}).
 *
 * Además, incorpora un campo de versión para control de concurrencia optimista,
 * evitando sobrescrituras en escenarios con múltiples actualizaciones
 * simultáneas.
 */
@Entity
@Table(name = "card_srs_state")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CardSrsState {

    /**
     * Identificador único del estado, coincidente con el identificador de la
     * tarjeta.
     */
    @Id
    @Column(name = "card_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID cardId;

    /**
     * Relación uno a uno con la tarjeta correspondiente.
     * Se comparte el mismo identificador mediante {@code @MapsId}, garantizando
     * que cada tarjeta tenga exactamente un estado asociado.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", referencedColumnName = "id", nullable = false)
    @MapsId
    @ToString.Exclude
    private Card card;

    /**
     * Número de versión para control de concurrencia optimista.
     * Incrementa automáticamente en cada actualización de la entidad, permitiendo
     * detectar modificaciones simultáneas.
     */
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    /**
     * Fecha y hora en la que la tarjeta debe volver a revisarse (en formato UTC).
     */
    @Column(name = "due_at", nullable = false)
    private OffsetDateTime dueAt;

    /** Intervalo actual en días hasta la próxima revisión. */
    @Column(name = "interval_days", nullable = false)
    @Min(0)
    private int intervalDays = 0;

    /**
     * Factor de facilidad utilizado por el algoritmo SRS para calcular el siguiente
     * intervalo.
     * Se inicializa normalmente en 2.50 y varía entre 1.30 y 3.00.
     */
    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    @DecimalMin(value = "1.30")
    @DecimalMax(value = "3.00")
    private BigDecimal easeFactor = new BigDecimal("2.50");

    /** Número total de repeticiones realizadas sobre la tarjeta. */
    @Column(name = "repetitions", nullable = false)
    @Min(0)
    private int repetitions = 0;

    /**
     * Resultado del último intento registrado.
     *
     * 0 indica fallo y 1 indica acierto.
     * Si en el futuro se adopta una escala de 0–5, deberá ajustarse la validación.
     */
    @Column(name = "last_result", nullable = false)
    @Min(0)
    @Max(1)
    private short lastResult = 0;

    /** Fecha y hora de la última actualización de este estado (en formato UTC). */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Inicializa las marcas temporales antes de la inserción si no se definieron
     * previamente.
     * También establece la fecha de vencimiento inicial en el momento actual si no
     * se ha asignado.
     */
    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        if (dueAt == null)
            dueAt = now;
        if (updatedAt == null)
            updatedAt = now;
    }

    /**
     * Actualiza la marca temporal de modificación antes de cada actualización en la
     * base de datos.
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}