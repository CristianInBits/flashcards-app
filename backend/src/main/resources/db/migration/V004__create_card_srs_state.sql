CREATE TABLE card_srs_state (
    card_id UUID PRIMARY KEY REFERENCES cards(id) ON DELETE CASCADE,
    due_at TIMESTAMPTZ NOT NULL,
    interval_days INT NOT NULL DEFAULT 0,
    ease_factor NUMERIC(4,2) NOT NULL DEFAULT 2.50,
    repetitions INT NOT NULL DEFAULT 0,
    last_result SMALLINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_srs_due_at ON card_srs_state(due_at);
-- Para filtrar por mazo eficientemente usaremos JOIN con cards:
-- SELECT c.id FROM cards c JOIN card_srs_state s ON s.card_id = c.id
-- WHERE c.deck_id = :deckId AND s.due_at <= now() ORDER BY s.due_at LIMIT :limit;