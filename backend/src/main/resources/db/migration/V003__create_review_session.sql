CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deck_id UUID NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at   TIMESTAMPTZ,
    total_cards INT NOT NULL DEFAULT 0,
    correct     INT NOT NULL DEFAULT 0,
    incorrect   INT NOT NULL DEFAULT 0,
    duration_sec INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_reviews_deck_started ON reviews(deck_id, started_at DESC);
