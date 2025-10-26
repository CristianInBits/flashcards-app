CREATE TABLE cards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deck_id UUID NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    front TEXT NOT NULL,
    back  TEXT NOT NULL,
    tags  TEXT,
    latex BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Índices para consultas típicas
CREATE INDEX idx_cards_deck_id ON cards(deck_id);
CREATE INDEX idx_cards_deck_created_at ON cards(deck_id, created_at DESC);

-- Checks para evitar blancos
ALTER TABLE cards
  ADD CONSTRAINT chk_cards_front_not_blank CHECK (btrim(front) <> ''),
  ADD CONSTRAINT chk_cards_back_not_blank  CHECK (btrim(back)  <> '');
