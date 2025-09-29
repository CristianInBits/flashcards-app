package com.csindila.flashcards.card.dto;

import jakarta.validation.constraints.NotBlank;

public record CardUpdateRequest(
        @NotBlank String front,
        @NotBlank String back,
        String tags,
        Boolean latex) {

}
