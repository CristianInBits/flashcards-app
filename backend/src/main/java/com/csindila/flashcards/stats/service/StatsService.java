package com.csindila.flashcards.stats.service;

import com.csindila.flashcards.deck.repo.DeckRepository;
import com.csindila.flashcards.stats.dto.*;
import com.csindila.flashcards.stats.repo.StatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

  private final StatsRepository stats;
  private final DeckRepository decks;

  public OverviewDto overview() {
    var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    var today = nowUtc.toLocalDate();
    var startOfToday = today.atStartOfDay().atOffset(ZoneOffset.UTC);
    var endOfToday = today.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

    long totalDecks = stats.countAllDecks();
    long totalCards = stats.countAllCards();
    long dueToday = stats.countDueToday(endOfToday);
    long reviewedToday = stats.countReviewedToday(startOfToday, endOfToday);

    var last7 = fillDaily(nowUtc, 7,
        stats.countDailySince(nowUtc.minusDays(6).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)));
    var last30 = fillDaily(nowUtc, 30,
        stats.countDailySince(nowUtc.minusDays(29).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)));

    return new OverviewDto(totalDecks, totalCards, dueToday, reviewedToday, last7, last30);
  }

  public DeckOverviewDto deckOverview(UUID deckId) {
    // Validamos existencia de deck para dar 404 coherente si no existe
    if (!decks.existsById(deckId))
      throw new NoSuchElementException("Deck no encontrado");

    var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    var today = nowUtc.toLocalDate();
    var startOfToday = today.atStartOfDay().atOffset(ZoneOffset.UTC);
    var endOfToday = today.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

    long totalCards = stats.countCardsByDeck(deckId);
    long dueToday = stats.countDueTodayByDeck(deckId, endOfToday);
    long reviewedToday = stats.countReviewedTodayByDeck(deckId, startOfToday, endOfToday);

    var last7 = fillDaily(nowUtc, 7,
        stats.countDailySinceByDeck(deckId,
            nowUtc.minusDays(6).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)));
    var last30 = fillDaily(nowUtc, 30,
        stats.countDailySinceByDeck(deckId,
            nowUtc.minusDays(29).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC)));

    return new DeckOverviewDto(deckId, totalCards, dueToday, reviewedToday, last7, last30);
  }

  private List<DailyCountDto> fillDaily(OffsetDateTime nowUtc, int daysWindow, List<Object[]> rows) {
    // Mapa day->count desde la query nativa (day puede venir como
    // Instant/Timestamp/OffsetDateTime/LocalDateTime/LocalDate)
    Map<LocalDate, Long> raw = rows.stream().collect(Collectors.toMap(
        r -> toUtcDate(r[0]),
        r -> ((Number) r[1]).longValue(),
        (a, b) -> a // merge fn por si acaso
    ));

    LocalDate end = nowUtc.toLocalDate();
    LocalDate start = end.minusDays(daysWindow - 1);

    return IntStream.range(0, daysWindow)
        .mapToObj(i -> start.plusDays(i))
        .map(day -> new DailyCountDto(day, raw.getOrDefault(day, 0L)))
        .toList();
  }

  private LocalDate toUtcDate(Object value) {
    if (value instanceof java.time.LocalDate ld) {
      return ld;
    }
    if (value instanceof java.sql.Date d) {
      return d.toLocalDate();
    }
    if (value instanceof java.sql.Timestamp ts) {
      return ts.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }
    if (value instanceof java.time.Instant ins) {
      return ins.atOffset(ZoneOffset.UTC).toLocalDate();
    }
    if (value instanceof java.time.OffsetDateTime odt) {
      return odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
    }
    if (value instanceof java.time.LocalDateTime ldt) {
      return ldt.atOffset(ZoneOffset.UTC).toLocalDate();
    }
    // Último recurso: intentamos parsear String (algunos drivers devuelven texto)
    if (value instanceof CharSequence cs) {
      return OffsetDateTime.parse(cs).toLocalDate();
    }
    throw new IllegalArgumentException("Tipo de fecha no soportado: " + value.getClass());
  }

}
