package com.csindila.flashcards.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewAnswerRequest(
        @Min(0) @Max(5) int grade) {
}