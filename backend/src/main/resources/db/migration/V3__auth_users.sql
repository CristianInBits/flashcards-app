CREATE TABLE app_user (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(72) NOT NULL, -- BCrypt ~60-72 chars
  role VARCHAR(20) NOT NULL,          -- 'USER' | 'ADMIN'
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_app_user_email ON app_user(email);

-- trigger timestamps opcional (si no, manejamos en JPA)
