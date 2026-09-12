package com.company.dakpion.dakpion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInitiateResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String redirectUrl;
    private String gatewaySessionId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
