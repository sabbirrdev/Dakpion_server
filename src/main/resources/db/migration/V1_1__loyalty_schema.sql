-- Loyalty Account Table
CREATE TABLE IF NOT EXISTS loyalty_account (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customer(id) ON DELETE CASCADE,
    current_points INT NOT NULL DEFAULT 0,
    lifetime_points_earned INT NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    entry_user BIGINT,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_user BIGINT,
    update_date TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_loyalty_account_customer ON loyalty_account(customer_id);

-- Loyalty Transaction Ledger Table
CREATE TABLE IF NOT EXISTS loyalty_transaction (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    type VARCHAR(20) NOT NULL,
    points INT NOT NULL,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    entry_user BIGINT,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_user BIGINT,
    update_date TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_loyalty_tx_customer ON loyalty_transaction(customer_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_tx_order ON loyalty_transaction(order_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_tx_order_type ON loyalty_transaction(order_id, type);
