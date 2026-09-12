package com.company.dakpion.dakpion.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates short codes (for letter short links) and tracking codes (for physical letters).
 * Uses URL-safe alphanumeric chars to avoid confusion between lookalike characters (0/O, 1/I/l).
 */
@Component
public class DakpionShortCodeGenerator {

    // Remove visually ambiguous chars: 0,O,I,1,l
    private static final String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghjkmnpqrstuvwxyz";
    private static final String TRACKING_CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates an 8-character alphanumeric short code for letter short links.
     * Example: "k9X2mQ8p"
     */
    public String generateShortCode() {
        return generateCode(CHARSET, 8);
    }

    /**
     * Generates a tracking code in the format "DP-XXXXXXXX".
     * Example: "DP-7F3K9Q2R"
     */
    public String generateTrackingCode() {
        return "DP-" + generateCode(TRACKING_CHARSET, 8);
    }

    private String generateCode(String charset, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(RANDOM.nextInt(charset.length())));
        }
        return sb.toString();
    }
}
