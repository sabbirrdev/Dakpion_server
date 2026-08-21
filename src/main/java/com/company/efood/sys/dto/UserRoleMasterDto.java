package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleMasterDto extends BaseDto {

    private static final long serialVersionUID = 1L;

    private Long id;

//    private Integer devCode;

}