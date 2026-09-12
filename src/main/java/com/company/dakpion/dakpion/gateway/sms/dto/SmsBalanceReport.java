package com.company.dakpion.dakpion.gateway.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsBalanceReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal balance;
    private String currency;
    private Integer errorCode;
    private String errorMessage;
}
