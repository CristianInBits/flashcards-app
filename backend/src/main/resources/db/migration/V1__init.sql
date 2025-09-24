CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE decks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deck_id UUID NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    front TEXT NOT NULL,
    back TEXT NOT NULL,
    tags TEXT,
    latex BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE reviews (
    card_id UUID PRIMARY KEY REFERENCES cards(id) ON DELETE CASCADE,
    due_at TIMESTAMPTZ NOT NULL,
    interval_days INT NOT NULL DEFAULT 0,
    ease NUMERIC(4,2) NOT NULL DEFAULT 2.50,
    reps INT NOT NULL DEFAULT 0,
    lapses INT NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMPTZ
);

CREATE TABLE review_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    card_id UUID NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    grade SMALLINT NOT NULL,
    prev_due_at TIMESTAMPTZ,
    new_due_at TIMESTAMPTZ,
    prev_interval INT,
    new_interval INT,
    prev_ease NUMERIC(4,2),
    new_ease NUMERIC(4,2)
);