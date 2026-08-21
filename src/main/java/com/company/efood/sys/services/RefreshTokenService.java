package com.company.efood.sys.services;

import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.RefreshTokenDto;

import java.util.Optional;

public interface RefreshTokenService extends BaseService<RefreshTokenDto> {

    boolean isRefreshTokenPresent(Long userId);
    Optional<RefreshTokenDto> findByToken(String token);
}
