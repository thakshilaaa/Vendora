-- Run once on an EXISTING MySQL `vendora` database if registration returns 500 / duplicate key errors
-- after enabling multiple accounts per email. Index names may differ — use SHOW INDEX FROM users;

USE vendora;

-- Allow more than one account with the same email (if still present)
-- ALTER TABLE users DROP INDEX email;

-- Allow more than one account with the same phone (if your DB was created from an older full schema)
-- ALTER TABLE users DROP INDEX phone;
