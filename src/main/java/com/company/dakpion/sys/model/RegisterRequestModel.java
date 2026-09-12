package com.company.dakpion.sys.model;

import lombok.Data;

@Data
public class RegisterRequestModel {
    private String displayName;
    private String username;
    private String password;
    private String phone;
}
