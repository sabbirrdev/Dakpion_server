package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiateRequestDto {

    private String letterId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be greater than or equal to 0")
    private BigDecimal amount;

    private PaymentMethod method;
    private String customerPhone;
    private String customerName;
}
