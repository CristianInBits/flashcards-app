package com.csindila.flashcards.deck.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeckDto(
                UUID id,
                String name,
                String description,
                OffsetDateTime createdAt) {
}
