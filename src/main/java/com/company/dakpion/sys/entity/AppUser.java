package com.company.dakpion.sys.entity;

import com.company.dakpion.base.BaseEntity;
import com.company.dakpion.sys.utils.AppUserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SYA_APP_USER")
public class AppUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "DISPLAY_NAME", length = 100)
    private String displayName;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "USER_TYPE_ID")
    private Integer userTypeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "APP_USER_TYPE", nullable = false)
    private AppUserType appUserType = AppUserType.USER;

    @Column(name = "IS_ACCOUNT_EXPIRED", columnDefinition = "boolean default false")
    private Boolean accountExpired = false;

    @Column(name = "IS_CREDENTIALS_EXPIRED", columnDefinition = "boolean default false")
    private Boolean credentialsExpired = false;

    @Column(name = "IS_ACCOUNT_LOCKED", columnDefinition = "boolean default false")
    private Boolean accountLocked = false;

    @Column(name = "OTP")
    private String otp;

    @Column(name = "OTP_EXPIRES_AT")
    private LocalDateTime otpExpiresAt;

    @Column(name = "PHONE_VERIFIED", columnDefinition = "boolean default false")
    private Boolean phoneVerified = false;

    @Column(name = "EMAIL_VERIFIED", columnDefinition = "boolean default false")
    private Boolean emailVerified = false;
}
