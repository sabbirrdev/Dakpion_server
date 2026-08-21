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
public class PasswordPolicyDto extends BaseDto implements Serializable {
    private String name;

    private Integer minLength;

    private Boolean sequential;

    private Boolean specialChar;

    private Boolean alphanumeric;

    private Boolean upperLower;

    private Boolean matchUsername;

    private Integer passwordRemember;

    private Integer passwordAge;

    private Integer devCode;
}
