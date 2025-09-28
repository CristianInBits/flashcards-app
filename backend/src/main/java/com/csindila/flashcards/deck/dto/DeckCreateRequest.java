package com.csindila.flashcards.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeckCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 10000) String description) {
}
