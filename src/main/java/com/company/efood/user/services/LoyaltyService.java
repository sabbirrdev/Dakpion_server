package com.company.efood.user.services;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.user.dto.LoyaltyRedeemQuoteDto;
import com.company.efood.user.dto.LoyaltySummaryDto;
import com.company.efood.user.dto.LoyaltyTransactionDto;
import com.company.efood.user.entity.LoyaltyAccount;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface LoyaltyService {

    LoyaltySummaryDto getLoyaltySummary(Long customerIdOrUserId);

    Page<LoyaltyTransactionDto> getLoyaltyHistory(Long customerIdOrUserId, BasePageableRequest request);

    LoyaltyRedeemQuoteDto calculateDiscountQuote(Long customerIdOrUserId, Integer requestedPoints, BigDecimal orderTotal);

    void awardPointsForOrder(Long orderId);

    void reversePointsForOrder(Long orderId);

    BigDecimal redeemPointsForOrder(Long customerId, Long orderId, Integer pointsToRedeem, BigDecimal orderTotal);

    void adjustPoints(Long customerId, Long orderId, int points, String description);

    LoyaltyAccount getOrCreateAccount(Long customerId);

    void expireOldPoints();
}
