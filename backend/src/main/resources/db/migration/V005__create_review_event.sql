CREATE TABLE review_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    review_id UUID REFERENCES reviews(id) ON DELETE SET NULL,
    card_id   UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    answered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    result SMALLINT NOT NULL,          -- 0 = fallo, 1 = acierto (o 0-5 futura)
    elapsed_ms INT NOT NULL DEFAULT 0,

    -- snapshots
    prev_due_at TIMESTAMPTZ,
    new_due_at  TIMESTAMPTZ,
    prev_interval INT,
    new_interval  INT,
    prev_ease NUMERIC(4,2),
    new_ease  NUMERIC(4,2)
);

CREATE INDEX idx_rev_events_card_time ON review_events(card_id, answered_at DESC);
CREATE INDEX idx_rev_events_review ON review_events(review_id);
