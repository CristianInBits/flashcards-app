-- Añade constraints de integridad para reviews, review_events y card_srs_state

BEGIN;

-- ============================================================
-- REVIEWS
-- - Evita negativos en contadores
-- - Asegura coherencia: total_cards = correct + incorrect
-- ============================================================
ALTER TABLE reviews
  ADD CONSTRAINT chk_reviews_nonneg
    CHECK (total_cards >= 0 AND correct >= 0 AND incorrect >= 0),
  ADD CONSTRAINT chk_reviews_totals
    CHECK (total_cards = correct + incorrect);

-- ============================================================
-- REVIEW_EVENTS
-- - Restringe el resultado a {0,1} (ajustable a 0..5 en el futuro)
-- - Evita tiempos negativos
-- ============================================================
ALTER TABLE review_events
  ADD CONSTRAINT chk_rev_events_result
    CHECK (result IN (0, 1)),
  ADD CONSTRAINT chk_rev_events_elapsed_nonneg
    CHECK (elapsed_ms >= 0);

-- ============================================================
-- CARD_SRS_STATE
-- - Evita negativos en intervalos y repeticiones
-- - Mantiene el ease factor dentro del rango 1.30..3.00 (SM-2)
-- - Restringe el último resultado a {0,1} (ajustable a 0..5)
-- ============================================================
ALTER TABLE card_srs_state
  ADD CONSTRAINT chk_srs_interval_nonneg
    CHECK (interval_days >= 0),
  ADD CONSTRAINT chk_srs_repetitions_nonneg
    CHECK (repetitions >= 0),
  ADD CONSTRAINT chk_srs_ease_range
    CHECK (ease_factor >= 1.30 AND ease_factor <= 3.00),
  ADD CONSTRAINT chk_srs_last_result
    CHECK (last_result IN (0, 1));

COMMIT;
