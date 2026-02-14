CREATE TABLE user (
  id CHAR(36) NOT NULL,
  active BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL,
  login VARCHAR(50) UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE INDEX idx_user_login ON user(login);
