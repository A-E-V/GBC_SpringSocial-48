-- Create the database separately
CREATE DATABASE userdb;

-- Connect to the database
\c userdb;

-- The rest of the commands can be in a transaction
BEGIN;

-- Create the user
CREATE USER myuser WITH PASSWORD 'password';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE userdb TO myuser;

-- Create the tables
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL, -- Consider hashing the password
    email VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100)
    );

-- Insert some data into users table
INSERT INTO users (username, password, email, full_name)
VALUES
    ('adrian', 'password1', 'adrian@example.com', 'Adrian Vargas'),
    ('nicholas', 'password2', 'nicholas@example.com', 'Nicholas Chiarello');

-- Commit the transaction
COMMIT;