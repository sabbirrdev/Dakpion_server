package com.company.dakpion.dakpion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookPayloadDto {
    private String tran_id;
    private String val_id;
    private BigDecimal amount;
    private String card_type;
    private String store_amount;
    private String card_no;
    private String bank_tran_id;
    private String status;
    private String tran_date;
    private String error;
    private String currency;
    private Map<String, Object> additionalParams;
}
