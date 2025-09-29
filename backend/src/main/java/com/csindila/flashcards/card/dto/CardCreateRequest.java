package com.csindila.flashcards.card.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CardCreateRequest(
        @NotNull UUID deckId,
        @NotBlank String front,
        @NotBlank String back,
        String tags,
        Boolean latex) {
}
