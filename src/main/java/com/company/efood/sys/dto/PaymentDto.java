package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.sys.utils.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentDto extends BaseDto {
    private String transactionId;
    private String paymentMethod;
    private Double amount;
    private PaymentStatus status;
    private Long orderId;
}
