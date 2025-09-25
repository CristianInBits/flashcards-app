-- Índices para acelerar consultas comunes
CREATE INDEX IF NOT EXISTS idx_cards_deck_id ON cards(deck_id);
CREATE INDEX IF NOT EXISTS idx_reviews_due_at ON reviews(due_at);
