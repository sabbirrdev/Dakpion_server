package com.company.dakpion.dakpion.gateway.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsDeliveryReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String requestStatus;
    private Integer errorCode;
    private String errorMessage;
    private List<RecipientReport> recipients;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipientReport implements Serializable {
        private static final long serialVersionUID = 1L;

        private String number;
        private BigDecimal charge;
        private String status;
        private String reason;
    }
}
