-- UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Decks table
CREATE TABLE decks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL CHECK (char_length(name) <= 100),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
