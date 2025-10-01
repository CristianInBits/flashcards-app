package com.csindila.flashcards.stats.dto;

import java.time.LocalDate;

public record DailyCountDto(
    LocalDate day,
    long count
) {}
