package dev.cristianinbits.flashcards.common.api;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbc;

    public HealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        try {
            Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
            return Map.of(
                    "app", "ok",
                    "db", "ok",
                    "select1", one);
        } catch (Exception e) {
            return Map.of(
                    "app", "ok",
                    "db", "fail",
                    "error", e.getMessage());
        }
    }
}
