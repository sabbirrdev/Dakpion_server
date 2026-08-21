package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleAssignDetailsDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userRoleId;
    private String userRoleName;

}