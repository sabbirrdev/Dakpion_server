package com.company.efood.sys.model;

import com.company.efood.sys.dto.UserRoleDetailsDto;
import com.company.efood.sys.dto.UserRoleMasterDto;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleModel {
    private UserRoleMasterDto userRoleMaster;
    private List<UserRoleDetailsDto> userRoleDetailsList;

}