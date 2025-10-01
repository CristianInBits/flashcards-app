package com.csindila.flashcards.stats.dto;

import java.util.List;

public record OverviewDto(
    long totalDecks,
    long totalCards,
    long dueToday,
    long reviewedToday,
    List<DailyCountDto> last7Days,
    List<DailyCountDto> last30Days
) {}
