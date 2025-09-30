package com.csindila.flashcards.review.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewDto(
    UUID cardId,
    OffsetDateTime dueAt,
    int intervalDays,
    double ease,
    int reps,
    int lapses,
    OffsetDateTime lastReviewedAt) {
}