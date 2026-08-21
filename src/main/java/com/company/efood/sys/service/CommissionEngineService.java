package com.company.efood.sys.service;

import com.company.efood.sys.entity.Commission;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.entity.Shop;
import com.company.efood.sys.repository.CommissionPolicyRepo;
import com.company.efood.sys.repository.CommissionRepo;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.sys.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CommissionEngineService {
    private final CommissionPolicyRepo policyRepo;
    private final CommissionRepo commissionRepo;
    private final OrderRepo orderRepo;
    private final ShopRepo shopRepo;

    public Commission calculateAndSave(Long orderId, Long shopId, String shopType, 
                                        BigDecimal orderTotal, BigDecimal deliveryFee) {
        BigDecimal rate = getCommissionRate(shopType);
        BigDecimal commissionAmount = orderTotal.multiply(rate).divide(BigDecimal.valueOf(100));
        BigDecimal merchantPayout = orderTotal.subtract(commissionAmount);

        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Shop shop = shopRepo.findById(shopId).orElseThrow(() -> new RuntimeException("Shop not found"));

        Commission c = new Commission();
        c.setOrder(order);
        c.setShop(shop);
        // c.setOrderId(orderId); // mapped as insertable=false, updatable=false
        c.setOrderTotal(orderTotal);
        c.setCommissionRate(rate);
        c.setCommissionAmount(commissionAmount);
        c.setDeliveryFee(deliveryFee);
        c.setMerchantPayout(merchantPayout);
        c.setShopType(shopType);
        c.setStatus("PENDING");
        c.setEntryUser(0L);
        c.setEntryDate(java.time.LocalDateTime.now());
        c.setActive(true);
        return commissionRepo.save(c);
    }

    private BigDecimal getCommissionRate(String shopType) {
        if (shopType == null) return BigDecimal.valueOf(10);
        return switch (shopType.toUpperCase()) {
            case "RESTAURANT" -> BigDecimal.valueOf(15);
            case "HOME_SERVICE" -> BigDecimal.valueOf(20);
            default -> BigDecimal.valueOf(10); // GROCERY, MEDICINE
        };
    }
}
