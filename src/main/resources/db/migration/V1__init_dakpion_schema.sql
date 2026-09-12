-- V1__init_dakpion_schema.sql
-- DakPion (ডাকপিওন) Enterprise Core Schema — Clean Baseline

-- 1. Application Users Table (Authentication, Profile & Admin Moderation)
CREATE TABLE IF NOT EXISTS sya_app_user (
    id BIGSERIAL PRIMARY KEY,
    active BOOLEAN DEFAULT TRUE,
    entry_user BIGINT,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_user BIGINT,
    update_date TIMESTAMP,
    username VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20),
    display_name VARCHAR(100),
    password VARCHAR(255),
    user_type_id INT,
    app_user_type VARCHAR(50) NOT NULL DEFAULT 'USER',
    is_account_expired BOOLEAN DEFAULT FALSE,
    is_credentials_expired BOOLEAN DEFAULT FALSE,
    is_account_locked BOOLEAN DEFAULT FALSE,
    otp VARCHAR(50),
    otp_expires_at TIMESTAMP,
    phone_verified BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_sya_app_user_username ON sya_app_user(username);
CREATE INDEX IF NOT EXISTS idx_sya_app_user_phone ON sya_app_user(phone);

-- 2. Refresh Token Table
CREATE TABLE IF NOT EXISTS sya_refresh_token (
    token VARCHAR(64) PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sya_app_user(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id ON sya_refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_expires ON sya_refresh_token(expires_at);

-- 3. OTP Session Table
CREATE TABLE IF NOT EXISTS dakpion_otp_session (
    request_id VARCHAR(64) PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    otp_code VARCHAR(10),
    attempts INT DEFAULT 0,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_otp_session_phone ON dakpion_otp_session(phone);
CREATE INDEX IF NOT EXISTS idx_otp_session_vtoken ON dakpion_otp_session(verification_token);
CREATE INDEX IF NOT EXISTS idx_otp_session_expires ON dakpion_otp_session(expires_at);

-- 4. Themes Table
CREATE TABLE IF NOT EXISTS dakpion_theme (
    id VARCHAR(50) PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_bn VARCHAR(255) NOT NULL,
    description_en TEXT NOT NULL,
    description_bn TEXT NOT NULL,
    tier VARCHAR(20) NOT NULL DEFAULT 'FREE',
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    palette JSONB NOT NULL,
    preview_image VARCHAR(255) NOT NULL,
    occasion_en VARCHAR(255),
    occasion_bn VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 5. Audio Tracks Table
CREATE TABLE IF NOT EXISTS dakpion_audio_track (
    id VARCHAR(50) PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_bn VARCHAR(255) NOT NULL,
    category_en VARCHAR(255) NOT NULL,
    category_bn VARCHAR(255) NOT NULL,
    src VARCHAR(500) NOT NULL,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    duration_seconds INT NOT NULL DEFAULT 0,
    is_premium BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 6. Delivery Options Table
CREATE TABLE IF NOT EXISTS dakpion_delivery_option (
    type VARCHAR(30) PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_bn VARCHAR(255) NOT NULL,
    description_en TEXT NOT NULL,
    description_bn TEXT NOT NULL,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    eta_label_en VARCHAR(255) NOT NULL,
    eta_label_bn VARCHAR(255) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 7. Pricing Plans Table
CREATE TABLE IF NOT EXISTS dakpion_pricing_plan (
    id VARCHAR(50) PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_bn VARCHAR(255) NOT NULL,
    tagline_en VARCHAR(255) NOT NULL,
    tagline_bn VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    billing_unit_en VARCHAR(255) NOT NULL,
    billing_unit_bn VARCHAR(255) NOT NULL,
    features JSONB NOT NULL,
    highlighted BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 8. Testimonials Table
CREATE TABLE IF NOT EXISTS dakpion_testimonial (
    id VARCHAR(50) PRIMARY KEY,
    author_nickname VARCHAR(255) NOT NULL,
    quote_en TEXT NOT NULL,
    quote_bn TEXT NOT NULL,
    city_en VARCHAR(255) NOT NULL,
    city_bn VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 9. FAQ Table
CREATE TABLE IF NOT EXISTS dakpion_faq (
    id VARCHAR(50) PRIMARY KEY,
    question_en TEXT NOT NULL,
    question_bn TEXT NOT NULL,
    answer_en TEXT NOT NULL,
    answer_bn TEXT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 10. Fonts & Typography Table
CREATE TABLE IF NOT EXISTS dakpion_font (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    font_family VARCHAR(255) NOT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'bengali',
    css_url VARCHAR(500),
    preview_sample VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 11. Blocked Words Table (Content Moderation)
CREATE TABLE IF NOT EXISTS dakpion_blocked_word (
    id BIGSERIAL PRIMARY KEY,
    term VARCHAR(255) NOT NULL UNIQUE,
    language VARCHAR(10) NOT NULL DEFAULT 'ALL',
    category VARCHAR(50) DEFAULT 'PROFANITY',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_dakpion_blocked_word_term ON dakpion_blocked_word(LOWER(term));

-- 12. Core Letters Table
CREATE TABLE IF NOT EXISTS dakpion_letter (
    id UUID PRIMARY KEY,
    sender_nickname VARCHAR(255) NOT NULL,
    sender_phone_hashed VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(50),
    recipient_user_id BIGINT REFERENCES sya_app_user(id) ON DELETE SET NULL,
    short_code VARCHAR(50) UNIQUE,
    tracking_code VARCHAR(50) UNIQUE,
    content TEXT NOT NULL,
    theme_id VARCHAR(50) NOT NULL REFERENCES dakpion_theme(id),
    audio_id VARCHAR(50) NOT NULL REFERENCES dakpion_audio_track(id),
    delivery_type VARCHAR(30) NOT NULL REFERENCES dakpion_delivery_option(type),
    shipping_address JSONB,
    theme_amount NUMERIC(10, 2),
    audio_amount NUMERIC(10, 2),
    delivery_amount NUMERIC(10, 2),
    total_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'BDT',
    payment_status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    moderation_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    moderation_reason TEXT,
    moderated_by BIGINT,
    moderated_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    idempotency_key VARCHAR(100),
    courier_booking_id VARCHAR(100),
    courier_tracking_id VARCHAR(100),
    courier_status VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    opened_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_dakpion_letter_sender_hash ON dakpion_letter(sender_phone_hashed);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_recipient_user ON dakpion_letter(recipient_user_id);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_short_code ON dakpion_letter(short_code);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_tracking_code ON dakpion_letter(tracking_code);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_mod_status ON dakpion_letter(moderation_status);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_status ON dakpion_letter(status);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_delivery_type ON dakpion_letter(delivery_type);
CREATE INDEX IF NOT EXISTS idx_dakpion_letter_created_at ON dakpion_letter(created_at DESC);
CREATE UNIQUE INDEX IF NOT EXISTS idx_dakpion_letter_idempotency ON dakpion_letter(idempotency_key) WHERE idempotency_key IS NOT NULL;

-- 13. Payment Transactions Table
CREATE TABLE IF NOT EXISTS dakpion_payment_transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    letter_id UUID UNIQUE REFERENCES dakpion_letter(id) ON DELETE SET NULL,
    provider VARCHAR(50) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'BDT',
    status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    gateway_reference VARCHAR(255),
    raw_payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_dakpion_payment_tx_letter ON dakpion_payment_transaction(letter_id);
CREATE INDEX IF NOT EXISTS idx_dakpion_payment_tx_status ON dakpion_payment_transaction(status);

-- 14. SMS Logs Table
CREATE TABLE IF NOT EXISTS dakpion_sms_log (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(100),
    to_number VARCHAR(20) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    message_content TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    error_code INTEGER,
    error_message TEXT,
    letter_id UUID REFERENCES dakpion_letter(id) ON DELETE SET NULL,
    charge NUMERIC(8, 4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_checked_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dakpion_sms_log_request_id ON dakpion_sms_log(request_id);
CREATE INDEX IF NOT EXISTS idx_dakpion_sms_log_status ON dakpion_sms_log(status);
CREATE INDEX IF NOT EXISTS idx_dakpion_sms_log_letter_id ON dakpion_sms_log(letter_id);
CREATE INDEX IF NOT EXISTS idx_dakpion_sms_log_created_at ON dakpion_sms_log(created_at);

-- 15. Delivery Events Table
CREATE TABLE IF NOT EXISTS dakpion_delivery_event (
    id BIGSERIAL PRIMARY KEY,
    letter_id UUID NOT NULL REFERENCES dakpion_letter(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    note TEXT,
    occurred_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dakpion_delivery_event_letter_id ON dakpion_delivery_event(letter_id);
CREATE INDEX IF NOT EXISTS idx_dakpion_delivery_event_occurred_at ON dakpion_delivery_event(occurred_at);
