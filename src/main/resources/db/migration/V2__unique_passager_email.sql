CREATE UNIQUE INDEX uq_passagers_email_ci
    ON passagers (LOWER(BTRIM(email)));
