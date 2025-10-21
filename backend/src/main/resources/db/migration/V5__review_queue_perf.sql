CREATE INDEX IF NOT EXISTS idx_reviews_due_at_asc ON reviews(due_at ASC);
CREATE INDEX IF NOT EXISTS idx_cards_deck_created_desc ON cards(deck_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_cards_deck_updated_desc ON cards(deck_id, updated_at DESC);
