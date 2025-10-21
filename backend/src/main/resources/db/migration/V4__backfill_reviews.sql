-- Crea reviews para cualquier card que aún no tenga su fila
INSERT INTO reviews (card_id, due_at, interval_days, ease, reps, lapses, last_reviewed_at)
SELECT c.id, now(), 0, 2.50, 0, 0, NULL
FROM cards c
LEFT JOIN reviews r ON r.card_id = c.id
WHERE r.card_id IS NULL;
