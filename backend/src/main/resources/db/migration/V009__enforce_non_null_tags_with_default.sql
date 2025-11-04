-- 1) Backfill: todo NULL o vacío pasa a 'general'
UPDATE cards SET tags = 'general' WHERE tags IS NULL OR BTRIM(tags) = '';

-- 2) Enforce NOT NULL y DEFAULT a nivel de BD
ALTER TABLE cards
    ALTER COLUMN tags SET DEFAULT 'general',
    ALTER COLUMN tags SET NOT NULL;

-- 3) CHECK para impedir cadenas vacías (solo espacios)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_cards_tags_not_blank'
          AND conrelid = 'cards'::regclass
    ) THEN
        ALTER TABLE cards
            ADD CONSTRAINT chk_cards_tags_not_blank CHECK (BTRIM(tags) <> '');
    END IF;
END$$;

-- (Opcional) normaliza a minúsculas lo existente (por coherencia)
UPDATE cards SET tags = LOWER(tags);
