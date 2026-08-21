package com.company.efood.sys.model;

import com.company.efood.sys.dto.UserRoleAssignDetailsDto;
import com.company.efood.sys.dto.UserRoleAssignMasterDto;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleAssignModel {
    private UserRoleAssignMasterDto master;
    private List<UserRoleAssignDetailsDto> detailsList;
}
