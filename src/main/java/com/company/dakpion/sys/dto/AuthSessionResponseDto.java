package com.company.dakpion.sys.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload returned on successful OTP-based authentication.
 * Contains both short-lived access token and long-lived refresh token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthSessionResponseDto {

    /** Short-lived JWT access token (15-30 min). */
    private String accessToken;

    /** Long-lived refresh token (30 days, stored in Redis). */
    private String refreshToken;

    /** Always "Bearer". */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Access token lifetime in seconds. */
    private long expiresIn;

    /** Stable user ID. */
    private Long userId;

    /** Username (may be null for phone-only accounts). */
    private String username;

    /** User's display name. */
    private String displayName;

    /** Masked phone for display: e.g. 01XX-XXX-678. */
    private String maskedPhone;

    /** User role (e.g. USER, ADMIN). */
    private String role;
}
