package com.company.efood.sys.services;

import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.AppUserDto;

public interface ApplicationUserService extends BaseService<AppUserDto> {
    AppUserDto getUserByUserId(Long userId);
}
