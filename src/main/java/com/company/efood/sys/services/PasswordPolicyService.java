package com.company.efood.sys.services;

import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.PasswordPolicyDto;

public interface PasswordPolicyService extends BaseService<PasswordPolicyDto> {

    PasswordPolicyDto getPublicPasswordPolicyById(Long id);

}
