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
public class UserRoleDetailsDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long menuItemId;
    private String menuItemName;
    private Boolean insert;
    private Boolean update;
    private Boolean delete;
    private Boolean approve;
    private Boolean view;

}

