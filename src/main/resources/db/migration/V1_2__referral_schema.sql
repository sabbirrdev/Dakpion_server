-- Referral Program: add referral_code, referred_by_id, referral_reward_credited to CUSTOMER table

ALTER TABLE customer ADD COLUMN IF NOT EXISTS referral_code VARCHAR(12) UNIQUE;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS referred_by_id BIGINT REFERENCES customer(id) ON DELETE SET NULL;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS referral_reward_credited BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_customer_referral_code ON customer(referral_code);

-- Back-fill a unique 8-char alphanumeric referral code for every existing customer who doesn't have one.
-- Uses md5 of customer id + random salt, trimmed to 8 uppercase chars.
UPDATE customer
SET referral_code = UPPER(SUBSTRING(MD5(id::text || random()::text) FROM 1 FOR 8))
WHERE referral_code IS NULL;
