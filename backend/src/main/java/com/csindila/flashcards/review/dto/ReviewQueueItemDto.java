package com.csindila.flashcards.review.dto;

import java.util.UUID;

public record ReviewQueueItemDto(
    UUID cardId,
    UUID deckId,
    String front,
    String back,
    String tags,
    boolean latex
) {}