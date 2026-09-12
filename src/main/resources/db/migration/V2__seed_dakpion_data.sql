-- V2__seed_dakpion_data.sql
-- Seed Data for DakPion (ডাকপিওন) — Clean Baseline

-- 1. Admin & Developer User Seed
INSERT INTO sya_app_user (username, password, display_name, phone, user_type_id, app_user_type, active, phone_verified, email_verified)
VALUES
('admin', '$2a$10$TonCORUUI8xrGJqD.T9BVuSrGySGE35lG7EN0ZT2GqPWs0/8fUqLq', 'System Admin', '01771243165', 1, 'ADMIN', TRUE, TRUE, TRUE),
('developer', '$2a$10$TonCORUUI8xrGJqD.T9BVuSrGySGE35lG7EN0ZT2GqPWs0/8fUqLq', 'Developer', '01708092483', 0, 'DEVELOPER', TRUE, TRUE, TRUE)
ON CONFLICT (username) DO NOTHING;

-- 2. Themes Seed
INSERT INTO dakpion_theme (id, name_en, name_bn, description_en, description_bn, tier, price, palette, preview_image, occasion_en, occasion_bn, active, display_order)
VALUES
(
    'plain_digital',
    'Plain Envelope',
    'সাধারণ খাম',
    'A clean, minimal digital envelope. No cost, no fuss.',
    'একদম সাদামাটা ডিজিটাল খাম। কোনো খরচ নেই, কোনো ঝামেলা নেই।',
    'FREE',
    0.00,
    '{"paperBg": "#F2E9D8", "paperTexture": "none", "ink": "#2B2118", "accent": "#7A8B6F", "envelope": "#EDE2CB", "seal": "#8A9A7E"}'::jsonb,
    'plain',
    NULL,
    NULL,
    TRUE,
    1
),
(
    'kraft_classic',
    'Kraft Paper',
    'ক্র্যাফট পেপার',
    'Light brown recycled-paper texture, for an everyday handwritten feel.',
    'হালকা বাদামী রিসাইকেলড কাগজের টেক্সচার, নিত্যদিনের হাতে লেখা অনুভূতির জন্য।',
    'FREE',
    0.00,
    '{"paperBg": "#E4D3B0", "paperTexture": "kraft", "ink": "#3A2E1F", "accent": "#A63D40", "envelope": "#C9A876", "seal": "#A63D40"}'::jsonb,
    'kraft',
    NULL,
    NULL,
    TRUE,
    2
),
(
    'vintage_premium_04',
    'Vintage Postmark',
    'ভিন্টেজ পোস্টমার্ক',
    'Aged parchment with a faded postmark stamp and deep ink borders.',
    'পুরনো পার্চমেন্ট কাগজ, ফিকে পোস্টমার্ক ছাপ আর গাঢ় কালির বর্ডার।',
    'PREMIUM',
    25.00,
    '{"paperBg": "#EDE0C0", "paperTexture": "parchment", "ink": "#1B2A4A", "accent": "#1B2A4A", "envelope": "#C9A876", "seal": "#A63D40"}'::jsonb,
    'vintage',
    'Timeless',
    'চিরন্তন',
    TRUE,
    3
),
(
    'falgun_bloom',
    'Falgun''s Bloom',
    'ফাল্গুনের ফুল',
    'Marigold and palash motifs along the border — for the first day of spring.',
    'বর্ডার জুড়ে গাঁদা আর পলাশ ফুলের ছাপ — পহেলা ফাল্গুনের জন্য।',
    'PREMIUM',
    20.00,
    '{"paperBg": "#FBEFE0", "paperTexture": "floral", "ink": "#5A2E1F", "accent": "#D9752B", "envelope": "#F2A65A", "seal": "#C1440E"}'::jsonb,
    'falgun',
    'Pohela Falgun',
    'পহেলা ফাল্গুন',
    TRUE,
    4
),
(
    'eid_lantern',
    'Eid Lantern',
    'ঈদ লণ্ঠন',
    'Deep emerald tones with gold lantern linework for Eid greetings.',
    'গাঢ় সবুজ রঙ আর সোনালি লণ্ঠনের নকশা, ঈদের শুভেচ্ছার জন্য।',
    'PREMIUM',
    25.00,
    '{"paperBg": "#F5EEDD", "paperTexture": "lantern", "ink": "#1F3A2E", "accent": "#B8892B", "envelope": "#1F3A2E", "seal": "#B8892B"}'::jsonb,
    'eid',
    'Eid',
    'ঈদ',
    TRUE,
    5
),
(
    'winter_mist',
    'Winter Mist',
    'শীতের কুয়াশা',
    'Cool grey-blue tones with a frosted border, for quiet winter mornings.',
    'শীতল ধূসর-নীল রঙ আর হালকা বরফের বর্ডার, নিরিবিলি শীতের সকালের জন্য।',
    'PREMIUM',
    20.00,
    '{"paperBg": "#E8EDF0", "paperTexture": "frost", "ink": "#2C3A4A", "accent": "#5C7A94", "envelope": "#B9C7D1", "seal": "#5C7A94"}'::jsonb,
    'winter',
    'Winter',
    'শীতকাল',
    TRUE,
    6
)
ON CONFLICT (id) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name_bn = EXCLUDED.name_bn,
    description_en = EXCLUDED.description_en,
    description_bn = EXCLUDED.description_bn,
    tier = EXCLUDED.tier,
    price = EXCLUDED.price,
    palette = EXCLUDED.palette,
    preview_image = EXCLUDED.preview_image,
    occasion_en = EXCLUDED.occasion_en,
    occasion_bn = EXCLUDED.occasion_bn;

-- 3. Audio Tracks Seed
INSERT INTO dakpion_audio_track (id, name_en, name_bn, category_en, category_bn, src, price, duration_seconds, is_premium, active, display_order)
VALUES
(
    'silence',
    'No Music',
    'সঙ্গীত ছাড়া',
    'Silence',
    'নীরবতা',
    '',
    0.00,
    0,
    FALSE,
    TRUE,
    1
),
(
    'rain_window',
    'Rain on the Window',
    'জানালায় বৃষ্টির শব্দ',
    'Ambient',
    'পরিবেশ ধ্বনি',
    '/audio/rain-window.mp3',
    0.00,
    128,
    FALSE,
    TRUE,
    2
),
(
    'gramophone_lofi_tune',
    'Old Gramophone Tune',
    'পুরনো দিনের গ্রামোফোন টিউন',
    'Nostalgia',
    'নস্টালজিয়া',
    '/audio/gramophone.mp3',
    0.00,
    95,
    FALSE,
    TRUE,
    3
),
(
    'romantic_flute',
    'Romantic Flute',
    'রোমান্টিক বাঁশির সুর',
    'Romantic',
    'রোমান্টিক',
    '/audio/flute.mp3',
    15.00,
    140,
    TRUE,
    TRUE,
    4
),
(
    'lofi_piano',
    'Lo-fi Piano',
    'লো-ফাই পিয়ানো',
    'Calm',
    'শান্ত',
    '/audio/piano.mp3',
    15.00,
    160,
    TRUE,
    TRUE,
    5
),
(
    'evening_crickets',
    'Village Evening',
    'গ্রামের সন্ধ্যা',
    'Ambient',
    'পরিবেশ ধ্বনি',
    '/audio/evening.mp3',
    0.00,
    110,
    FALSE,
    TRUE,
    6
)
ON CONFLICT (id) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name_bn = EXCLUDED.name_bn,
    category_en = EXCLUDED.category_en,
    category_bn = EXCLUDED.category_bn,
    src = EXCLUDED.src,
    price = EXCLUDED.price,
    duration_seconds = EXCLUDED.duration_seconds,
    is_premium = EXCLUDED.is_premium;

-- 4. Delivery Options Seed
INSERT INTO dakpion_delivery_option (type, name_en, name_bn, description_en, description_bn, price, eta_label_en, eta_label_bn, icon, active, display_order)
VALUES
(
    'DIGITAL',
    'Digital Link',
    'ডিজিটাল লিংক',
    'A unique secret link you share yourself, however you like.',
    'একটি ইউনিক সিক্রেট লিংক যা আপনি নিজের ইচ্ছেমতো শেয়ার করবেন।',
    0.00,
    'Instant',
    'সাথে সাথে',
    'link',
    TRUE,
    1
),
(
    'SMS_SPEED_POST',
    'Speed Post (SMS)',
    'স্পিড পোস্ট (এসএমএস)',
    'We send an anonymous SMS alert straight to the recipient’s phone.',
    'আমরা প্রাপকের ফোনে সরাসরি একটি বেনামী এসএমএস অ্যালার্ট পাঠাই।',
    10.00,
    'Within minutes',
    'কয়েক মিনিটের মধ্যে',
    'sms',
    TRUE,
    2
),
(
    'PHYSICAL',
    'Physical DakPion',
    'ফিজিক্যাল ডাকপিওন',
    'Printed on vintage paper, sealed with wax, and couriered to their door.',
    'ভিন্টেজ কাগজে প্রিন্ট করে, মোম দিয়ে সিলগালা করে সরাসরি ঠিকানায় কুরিয়ার করা হয়।',
    120.00,
    '2–4 business days',
    '২-৪ কর্মদিবস',
    'courier',
    TRUE,
    3
)
ON CONFLICT (type) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name_bn = EXCLUDED.name_bn,
    description_en = EXCLUDED.description_en,
    description_bn = EXCLUDED.description_bn,
    price = EXCLUDED.price,
    eta_label_en = EXCLUDED.eta_label_en,
    eta_label_bn = EXCLUDED.eta_label_bn,
    icon = EXCLUDED.icon;

-- 5. Pricing Plans Seed
INSERT INTO dakpion_pricing_plan (id, name_en, name_bn, tagline_en, tagline_bn, price, billing_unit_en, billing_unit_bn, features, highlighted, active, display_order)
VALUES
(
    'free',
    'Free',
    'ফ্রি',
    'For your first letter',
    'আপনার প্রথম চিঠির জন্য',
    0.00,
    'per letter',
    'প্রতি চিঠি',
    '[
        {"en": "Plain & kraft paper themes", "bn": "প্লেইন ও ক্র্যাফট পেপার থিম"},
        {"en": "3 free ambient tracks", "bn": "৩টি ফ্রি অ্যাম্বিয়েন্ট মিউজিক"},
        {"en": "Digital link delivery", "bn": "ডিজিটাল লিংক ডেলিভারি"},
        {"en": "OTP-verified sending", "bn": "ওটিপি-যাচাইকৃত পাঠানো"}
    ]'::jsonb,
    FALSE,
    TRUE,
    1
),
(
    'premium_letter',
    'Premium Letter',
    'প্রিমিয়াম চিঠি',
    'Dress it up for the occasion',
    'উপলক্ষ অনুযায়ী সাজিয়ে নিন',
    25.00,
    'per letter',
    'প্রতি চিঠি',
    '[
        {"en": "All vintage & seasonal themes", "bn": "সব ভিন্টেজ ও মৌসুমি থিম"},
        {"en": "Full music library incl. flute & piano", "bn": "বাঁশি ও পিয়ানোসহ সম্পূর্ণ মিউজিক লাইব্রেরি"},
        {"en": "Wax-seal envelope animation", "bn": "মোম-সিল খাম অ্যানিমেশন"},
        {"en": "Priority moderation review", "bn": "অগ্রাধিকার মডারেশন রিভিউ"}
    ]'::jsonb,
    TRUE,
    TRUE,
    2
),
(
    'physical_bundle',
    'Physical DakPion',
    'ফিজিক্যাল ডাকপিওন',
    'A real letter, at their door',
    'সত্যিকারের চিঠি, তাদের দরজায়',
    120.00,
    'per delivery',
    'প্রতি ডেলিভারি',
    '[
        {"en": "Printed on vintage stock", "bn": "ভিন্টেজ কাগজে প্রিন্ট"},
        {"en": "Real wax seal", "bn": "আসল মোমের সিল"},
        {"en": "Nationwide courier tracking", "bn": "দেশজুড়ে কুরিয়ার ট্র্যাকিং"},
        {"en": "Includes Premium theme & music", "bn": "প্রিমিয়াম থিম ও মিউজিক অন্তর্ভুক্ত"}
    ]'::jsonb,
    FALSE,
    TRUE,
    3
)
ON CONFLICT (id) DO UPDATE SET
    name_en = EXCLUDED.name_en,
    name_bn = EXCLUDED.name_bn,
    tagline_en = EXCLUDED.tagline_en,
    tagline_bn = EXCLUDED.tagline_bn,
    price = EXCLUDED.price,
    billing_unit_en = EXCLUDED.billing_unit_en,
    billing_unit_bn = EXCLUDED.billing_unit_bn,
    features = EXCLUDED.features,
    highlighted = EXCLUDED.highlighted;

-- 6. Testimonials Seed
INSERT INTO dakpion_testimonial (id, author_nickname, quote_en, quote_bn, city_en, city_bn, active, display_order)
VALUES
(
    't1',
    'অচেনা পথিক',
    'I hadn’t written a letter by hand in ten years. This made me cry a little, in a good way.',
    'দশ বছর ধরে হাতে চিঠি লিখিনি। এটা আমাকে একটু কাঁদিয়েছে, ভালো লাগায়।',
    'Dhaka',
    'ঢাকা',
    TRUE,
    1
),
(
    't2',
    'নীল আকাশ',
    'Sent a physical letter to my grandmother in Rajshahi. She called me the moment it arrived.',
    'রাজশাহীতে আমার দাদির কাছে একটা ফিজিক্যাল চিঠি পাঠিয়েছিলাম। পৌঁছানোর সাথে সাথেই ফোন দিলেন।',
    'Rajshahi',
    'রাজশাহী',
    TRUE,
    2
),
(
    't3',
    'বৃষ্টিভেজা',
    'The envelope-opening animation with the rain sound is the most delightful five seconds on the internet.',
    'বৃষ্টির শব্দসহ খাম খোলার অ্যানিমেশনটা ইন্টারনেটের সবচেয়ে সুন্দর পাঁচ সেকেন্ড।',
    'Chattogram',
    'চট্টগ্রাম',
    TRUE,
    3
)
ON CONFLICT (id) DO UPDATE SET
    author_nickname = EXCLUDED.author_nickname,
    quote_en = EXCLUDED.quote_en,
    quote_bn = EXCLUDED.quote_bn,
    city_en = EXCLUDED.city_en,
    city_bn = EXCLUDED.city_bn;

-- 7. FAQ Seed
INSERT INTO dakpion_faq (id, question_en, question_bn, answer_en, answer_bn, active, display_order)
VALUES
(
    'f1',
    'Do I need an account to write a letter?',
    'চিঠি লিখতে কি অ্যাকাউন্ট লাগবে?',
    'No. Writing is completely guest-mode — no login required. We only ask for a phone number to verify with an OTP right before sending.',
    'না। লেখাটা সম্পূর্ণ গেস্ট মোডে হয় — লগইন লাগে না। পাঠানোর ঠিক আগে শুধু ওটিপি যাচাইয়ের জন্য একটি ফোন নম্বর চাওয়া হয়।',
    TRUE,
    1
),
(
    'f2',
    'Will the recipient know who sent it?',
    'প্রাপক কি জানবেন কে পাঠিয়েছে?',
    'Only if you tell them. You can sign with any nickname, and your real phone number is hashed and never shown.',
    'শুধুমাত্র যদি আপনি নিজে বলেন। আপনি যেকোনো ছদ্মনামে সই করতে পারেন, আর আপনার আসল ফোন নম্বর হ্যাশ করা থাকে, কখনো দেখানো হয় না।',
    TRUE,
    2
),
(
    'f3',
    'How does the Physical DakPion delivery work?',
    'ফিজিক্যাল ডাকপিওন ডেলিভারি কীভাবে কাজ করে?',
    'After payment, our team prints your letter on vintage paper, seals it with real wax, and books a courier pickup — typically arriving in 2–4 business days.',
    'পেমেন্টের পর আমাদের টিম আপনার চিঠি ভিন্টেজ কাগজে প্রিন্ট করে, আসল মোম দিয়ে সিল করে, এবং কুরিয়ার বুক করে — সাধারণত ২-৪ কর্মদিবসের মধ্যে পৌঁছায়।',
    TRUE,
    3
),
(
    'f4',
    'Is my letter moderated before delivery?',
    'ডেলিভারির আগে কি আমার চিঠি মডারেট করা হয়?',
    'A lightweight automated filter checks for abusive language at submission. Physical deliveries also get a quick human review before printing.',
    'সাবমিশনের সময় একটি হালকা অটোমেটেড ফিল্টার আপত্তিকর ভাষা যাচাই করে। ফিজিক্যাল ডেলিভারির ক্ষেত্রে প্রিন্টের আগে দ্রুত মানুষের রিভিউও হয়।',
    TRUE,
    4
)
ON CONFLICT (id) DO UPDATE SET
    question_en = EXCLUDED.question_en,
    question_bn = EXCLUDED.question_bn,
    answer_en = EXCLUDED.answer_en,
    answer_bn = EXCLUDED.answer_bn;

-- 8. Fonts & Typography Seed
INSERT INTO dakpion_font (id, name, font_family, category, css_url, preview_sample, active, display_order)
VALUES
('kalpana-unicode', 'Kalpana (কল্পনা ইউনিকোড)', '''Kalpana'', ''Kalpana UNICODE'', serif', 'bengali', NULL, 'কল্পনার রঙিন চিঠি', TRUE, 1),
('solaiman-lipi', 'SolaimanLipi (সোলাইমান)', '''SolaimanLipi'', ''Hind Siliguri'', sans-serif', 'bengali', NULL, 'ডাকপিওনের বার্তা', TRUE, 2),
('tiro-bangla', 'Tiro Bangla (তিরো)', '''Tiro Bangla'', serif', 'bengali', 'https://fonts.googleapis.com/css2?family=Tiro+Bangla:ital@0;1&display=swap', 'আমার সোনার বাংলা', TRUE, 3),
('hind-siliguri', 'Hind Siliguri (শিলিগুড়ি)', '''Hind Siliguri'', sans-serif', 'bengali', 'https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;500;600;700&display=swap', 'চিঠির পাতায় স্মৃতি', TRUE, 4),
('kalpurush', 'Kalpurush (কালপুরুষ)', '''Kalpurush'', ''Tiro Bangla'', serif', 'bengali', 'https://fonts.maateen.me/kalpurush/font.css', 'একমুঠো ভালোবাসা', TRUE, 5),
('fraunces', 'Fraunces (Vintage Serif)', '''Fraunces'', serif', 'latin', 'https://fonts.googleapis.com/css2?family=Fraunces:ital,opsz,wght@0,9..144,400;0,9..144,600;1,9..144,400&display=swap', 'Nostalgic Letters', TRUE, 6),
('inter', 'Inter (Modern Sans)', '''Inter'', sans-serif', 'latin', 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap', 'Clean & Timeless', TRUE, 7)
ON CONFLICT (id) DO NOTHING;

-- 9. Blocked Words Seed (Moderation Filter)
INSERT INTO dakpion_blocked_word (term, language, category)
VALUES
('idiot', 'en', 'PROFANITY'),
('stupid', 'en', 'PROFANITY'),
('hate you', 'en', 'HARASSMENT'),
('kill you', 'en', 'VIOLENCE'),
('বোকা', 'bn', 'PROFANITY'),
('মূর্খ', 'bn', 'PROFANITY'),
('ঘৃণা করি', 'bn', 'HARASSMENT')
ON CONFLICT (term) DO NOTHING;
