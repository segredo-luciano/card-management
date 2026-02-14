CREATE TABLE card (
    id CHAR(36) NOT NULL,
	active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    batch_id BIGINT,
    order_number INT,

    card_number VARBINARY(512) NOT NULL,
    card_hash CHAR(64) NOT NULL,

    CONSTRAINT uk_card_hash UNIQUE (card_hash)
);

CREATE INDEX idx_card_hash_active ON card(card_hash, active);
CREATE INDEX idx_card_batch_order ON card(batch_id, order_number);