package com.csindila.flashcards.stats.web;

import com.csindila.flashcards.stats.dto.*;
import com.csindila.flashcards.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    /**
     * Estadísticas globales de la app.
     */
    @GetMapping("/overview")
    public OverviewDto overview() {
        return service.overview();
    }

    /**
     * Estadísticas para un deck concreto.
     */
    @GetMapping("/decks/{deckId}/overview")
    public DeckOverviewDto deckOverview(@PathVariable UUID deckId) {
        return service.deckOverview(deckId);
    }
}
