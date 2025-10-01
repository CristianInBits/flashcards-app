package com.csindila.flashcards.review.domain;

import java.math.BigDecimal;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.csindila.flashcards.card.domain.Card;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {

    @Id
    @Column(name = "card_id")
    private UUID id; // PK = card_id

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "due_at", nullable = false)
    private OffsetDateTime dueAt;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays = 0;

    @Column(name = "ease", nullable = false, precision = 4, scale = 2)
    private BigDecimal ease;

    @Column(name = "reps", nullable = false)
    private int reps = 0;

    @Column(name = "lapses", nullable = false)
    private int lapses = 0;

    @Column(name = "last_reviewed_at")
    private OffsetDateTime lastReviewedAt;

    @PrePersist
    void prePersist() {
        if (dueAt == null)
            dueAt = OffsetDateTime.now(ZoneOffset.UTC);
        if (ease == null)
            ease = new BigDecimal("2.50"); // valor por defecto del SRS
    }
}
