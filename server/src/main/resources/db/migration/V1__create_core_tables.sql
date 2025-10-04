 -- Enable helpers for UUIDs and case-insensitive emails
  CREATE EXTENSION IF NOT EXISTS "pgcrypto";
  CREATE EXTENSION IF NOT EXISTS "citext";

  -- Enumerations used across tables
  CREATE TYPE account_type AS ENUM ('BANK', 'CREDIT_CARD');
  CREATE TYPE transaction_type AS ENUM ('INCOME', 'EXPENSE', 'TRANSFER');
  CREATE TYPE category_kind AS ENUM ('INCOME', 'EXPENSE');

  CREATE TABLE users (
      id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      email           CITEXT NOT NULL,
      password_hash   TEXT NOT NULL,
      is_active       BOOLEAN NOT NULL DEFAULT TRUE,
      created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
  );

  CREATE UNIQUE INDEX users_email_unique_ci ON users (email);

  CREATE TABLE accounts (
      id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      name            TEXT NOT NULL,
      type            account_type NOT NULL,
      currency_code   CHAR(3) NOT NULL,
      is_active       BOOLEAN NOT NULL DEFAULT TRUE,
      credit_limit    NUMERIC(14,2),
      closing_day     SMALLINT,
      due_day         SMALLINT,
      created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT accounts_type_card_fields CHECK (
          (type = 'BANK' AND credit_limit IS NULL AND closing_day IS NULL AND due_day IS NULL)
          OR
          (type = 'CREDIT_CARD' AND closing_day BETWEEN 1 AND 31 AND due_day BETWEEN 1 AND 31)
      )
  );

  CREATE UNIQUE INDEX accounts_user_name_unique ON accounts (user_id, name);

  CREATE TABLE categories (
      id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      name            TEXT NOT NULL,
      kind            category_kind NOT NULL,
      color           TEXT,
      icon            TEXT,
      created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
  );

  CREATE UNIQUE INDEX categories_user_name_kind_unique ON categories (user_id, name, kind);

  CREATE TABLE transactions (
      id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      user_id                UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      type                   transaction_type NOT NULL,
      amount                 NUMERIC(14,2) NOT NULL CHECK (amount > 0),
      event_date             DATE NOT NULL,
      description            TEXT,
      account_id             UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
      counter_account_id     UUID REFERENCES accounts(id) ON DELETE CASCADE,
      category_id            UUID REFERENCES categories(id) ON DELETE SET NULL,
      tags                   TEXT[],
      created_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at             TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
      CONSTRAINT transactions_transfer_counter_account CHECK (
          (type = 'TRANSFER' AND counter_account_id IS NOT NULL)
          OR
          (type <> 'TRANSFER' AND counter_account_id IS NULL)
      )
  );

  CREATE INDEX transactions_user_event_date_idx ON transactions (user_id, event_date);
  CREATE INDEX transactions_account_event_date_idx ON transactions (account_id, event_date);
