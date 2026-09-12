package com.company.dakpion.sys.dto;

import com.company.dakpion.base.BaseDto;
import com.company.dakpion.sys.utils.AppUserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String phone;
    private String displayName;
    private String password;
    private Long userTypeId;
    private AppUserType appUserType;
    private String otp;
    private Boolean phoneVerified;
    private Boolean emailVerified;

}
