package com.company.efood.sys.services;

import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.UserRoleMasterDto;
import com.company.efood.sys.model.UserRoleModel;

import java.util.List;

public interface UserRoleService extends BaseService<UserRoleModel> {
    List<UserRoleMasterDto> getRoleListByUser(Long appUserId, Long userId);
}
