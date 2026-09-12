package com.company.dakpion.base;

import lombok.Builder;
import lombok.Data;

/**
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 * @version 1.0.0
 */
@Data
@Builder
public class BaseResponse {
    private int statusCode;
    private Boolean status;
    private String message;
    private String messageBn;
    private Object data;
}
