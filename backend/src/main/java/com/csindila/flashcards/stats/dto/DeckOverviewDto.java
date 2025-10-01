package com.csindila.flashcards.stats.dto;

import java.util.List;
import java.util.UUID;

public record DeckOverviewDto(
    UUID deckId,
    long totalCards,
    long dueToday,
    long reviewedToday,
    List<DailyCountDto> last7Days,
    List<DailyCountDto> last30Days
) {}
