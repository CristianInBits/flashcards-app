package com.csindila.flashcards.review.domain;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.csindila.flashcards.card.domain.Card;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "review_log")
@Getter
@Setter
public class ReviewLog {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "reviewed_at", nullable = false)
    private OffsetDateTime reviewedAt;

    @Column(name = "grade", nullable = false)
    private short grade;

    @Column(name = "prev_due_at")
    private OffsetDateTime prevDueAt;

    @Column(name = "new_due_at")
    private OffsetDateTime newDueAt;

    @Column(name = "prev_interval")
    private Integer prevInterval;

    @Column(name = "new_interval")
    private Integer newInterval;

    @Column(name = "prev_ease")
    private Double prevEase;

    @Column(name = "new_ease")
    private Double newEase;

    @PrePersist
    void prePersist() {
        if (id == null)
            id = UUID.randomUUID(); // generamos en app
        if (reviewedAt == null)
            reviewedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}