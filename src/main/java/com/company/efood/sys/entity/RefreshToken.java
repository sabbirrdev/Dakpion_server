package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "SYA_REFRESH_TOKEN")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseEntity {
    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;
    @Column(name = "EXPIRE_TIME")
    private Instant expireTime;

    @JoinColumn(name = "APP_USER_ID")
    private Integer appUserId;

}
