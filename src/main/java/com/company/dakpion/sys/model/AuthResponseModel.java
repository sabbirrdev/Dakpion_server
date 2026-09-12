package com.company.dakpion.sys.model;

import com.company.dakpion.sys.utils.AppUserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @version 1.0.0
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseModel {
    private String accessToken;
    private String refreshToken;
    private Integer userTypeId;
    private AppUserType appUserType;
    private long loginTime;
    private long loginExpierDuration;
}
