package com.company.dakpion.dakpion.gateway.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsSendResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String requestId;
    private Integer errorCode;
    private String errorMessage;
    private String rawResponse;
}
