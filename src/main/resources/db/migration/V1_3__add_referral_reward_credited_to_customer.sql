-- Migration: Add referral_reward_credited column to CUSTOMER table
ALTER TABLE customer ADD COLUMN IF NOT EXISTS referral_reward_credited BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index if querying by referral_reward_credited
CREATE INDEX IF NOT EXISTS idx_customer_referral_reward ON customer(referral_reward_credited);
