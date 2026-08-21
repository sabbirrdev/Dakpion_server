package com.company.efood.user.listener;

import com.company.efood.sys.event.OrderStatusChangedEvent;
import com.company.efood.sys.utils.OrderStatus;
import com.company.efood.user.services.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoyaltyEventListener {

    private final LoyaltyService loyaltyService;
    private final com.company.efood.user.services.ReferralService referralService;
    private final com.company.efood.sys.repository.OrderRepo orderRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderStatusChange(OrderStatusChangedEvent event) {
        Long orderId = event.getOrderId();
        OrderStatus newStatus = event.getNewStatus();
        log.info("Received OrderStatusChangedEvent for order #{} -> {}", orderId, newStatus);

        try {
            if (newStatus == OrderStatus.COMPLETED) {
                loyaltyService.awardPointsForOrder(orderId);
                // Trigger referral reward check if referee's first order completed
                orderRepo.findById(orderId).ifPresent(order -> {
                    if (order.getCustomer() != null) {
                        referralService.creditReferralBonusIfEligible(order.getCustomer().getId(), orderId);
                    }
                });
            } else if (newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.REJECTED) {
                loyaltyService.reversePointsForOrder(orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process loyalty points / referral event for order #{}: {}", orderId, e.getMessage(), e);
        }
    }
}
