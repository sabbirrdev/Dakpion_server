package com.company.dakpion.sys.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SecurityUtils {
    // ── helpers ────────────────────────────────────────────────────────────────

    /**
     * Masks phone to 01XX-XXX-678 format.
     * Never returns a raw phone number — PII safety for API responses.
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            // 01XXXXXXXXX → 01XX-XXX-678
            return digits.substring(0, 2) + "XX-XXX-" + digits.substring(8);
        }
        // fallback for unexpected length
        if (digits.length() > 6) {
            return digits.substring(0, 2) + "X".repeat(digits.length() - 5) + digits.substring(digits.length() - 3);
        }
        return "XXXXXX";
    }

    public static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        String clean = phone.replaceAll("[^0-9+]", "");
        if (clean.startsWith("+880")) clean = "0" + clean.substring(4);
        else if (clean.startsWith("880")) clean = "0" + clean.substring(3);
        else if (clean.length() == 10 && clean.startsWith("1")) clean = "0" + clean;

        if (!clean.matches("^01[3-9]\\d{8}$")) {
            throw new IllegalArgumentException("Invalid Bangladeshi mobile number: " + phone);
        }
        return clean;
    }

    public static String hashPhoneWithPepper(String phone, String pepper) {
        try {
            String normalized = phone.replaceAll("[^0-9+]", "");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((normalized + ":" + pepper).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String fullHex = hexString.toString();
            return fullHex.substring(0, 4) + "..." + fullHex.substring(fullHex.length() - 4);
        } catch (NoSuchAlgorithmException e) {
            return "anon-hash-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
