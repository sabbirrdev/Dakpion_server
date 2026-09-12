package com.company.dakpion.dakpion.dto;
import com.company.dakpion.base.BaseDto;
import com.company.dakpion.dakpion.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentDto extends BaseDto {
    private String transactionId;
    private String paymentMethod;
    private Double amount;
    private PaymentStatus status;
    private UUID orderId;
}
