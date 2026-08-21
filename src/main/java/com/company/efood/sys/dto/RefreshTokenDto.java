package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshTokenDto extends BaseDto implements Serializable {

    private String refreshToken;
    private Instant expireTime;
    private Long appUserId;

}
