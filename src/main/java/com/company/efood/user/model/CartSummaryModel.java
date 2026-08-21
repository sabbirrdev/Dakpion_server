package com.company.efood.user.model;

import java.math.BigDecimal;

public interface CartSummaryModel {
    BigDecimal getSubtotal();
    BigDecimal getDeliveryFee();
    BigDecimal getDiscount();
    BigDecimal getTax();
    BigDecimal getTotalPayable();
    String getAppliedCoupon();
}
