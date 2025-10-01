-- V3__remove_password_column.sql
START TRANSACTION;

-- Remove password column from users table
ALTER TABLE users DROP COLUMN IF EXISTS password;

COMMIT; 