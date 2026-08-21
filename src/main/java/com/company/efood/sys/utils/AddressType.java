package com.company.efood.sys.utils;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AddressType {
    DEFAULT, HOME_ADDRESS, SHOP_ADDRESS, OFFICE_ADDRESS;

    @JsonCreator
    public static AddressType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT;
        }
        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "HOME", "HOME_ADDRESS" -> HOME_ADDRESS;
            case "SHOP", "SHOP_ADDRESS" -> SHOP_ADDRESS;
            case "OFFICE", "OFFICE_ADDRESS", "WORK" -> OFFICE_ADDRESS;
            default -> DEFAULT;
        };
    }
}
