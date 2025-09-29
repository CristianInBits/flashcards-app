package com.csindila.flashcards.card.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CardDto(
        UUID id,
        UUID deckId,
        String front,
        String back,
        String tags,
        boolean latex,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

}
