package com.company.dakpion.dakpion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dakpion_otp_session")
public class DakpionOtpSessionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "request_id", length = 64, nullable = false)
    private String requestId;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "code_hash", length = 128, nullable = false)
    private String codeHash;

    @Column(name = "salt", length = 64, nullable = false)
    private String salt;

    @Column(name = "otp_code", length = 10)
    private String otpCode;

    @Builder.Default
    @Column(name = "attempts")
    private Integer attempts = 0;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verification_token", length = 64)
    private String verificationToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
