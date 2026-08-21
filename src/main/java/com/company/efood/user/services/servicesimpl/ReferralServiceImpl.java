package com.company.efood.user.services.servicesimpl;

import com.company.efood.base.BaseUtils;
import com.company.efood.user.dto.ReferralInfoDto;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.entity.LoyaltyTransactionType;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.repository.LoyaltyAccountRepo;
import com.company.efood.user.repository.LoyaltyTransactionRepo;
import com.company.efood.user.services.LoyaltyService;
import com.company.efood.user.services.ReferralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // avoids confusable chars
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CustomerRepo customerRepo;
    private final LoyaltyAccountRepo loyaltyAccountRepo;
    private final LoyaltyTransactionRepo loyaltyTransactionRepo;
    private final LoyaltyService loyaltyService;
    private final BaseUtils baseUtils;

    @Value("${referral.referrer-bonus-points:50}")
    private int referrerBonus;

    @Value("${referral.referee-bonus-points:30}")
    private int refereeBonus;

    @Value("${referral.share-base-url:efood://referral?code=}")
    private String shareBaseUrl;

    @Value("${referral.max-referrals-per-day:10}")
    private int maxReferralsPerDay;

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    @Override
    @Transactional
    public void initReferral(Customer customer, String incomingReferralCode) {
        // 1. Generate and assign a unique code for this new customer.
        String myCode = generateUniqueCode();
        customer.setReferralCode(myCode);

        // 2. If a valid referral code was supplied, link the referrer.
        if (incomingReferralCode != null && !incomingReferralCode.isBlank()) {
            String normalised = incomingReferralCode.trim().toUpperCase();

            // Prevent self-referral (edge case where code was typed from a screenshot).
            if (normalised.equals(myCode)) {
                log.warn("Customer tried to self-refer using code {}. Ignored.", myCode);
            } else {
                customerRepo.findByReferralCode(normalised).ifPresent(referrer -> {
                    // Prevent duplicate referral linking
                    if (customer.getReferredBy() == null) {
                        customer.setReferredBy(referrer);
                        log.info("Customer #{} was referred by customer #{} (code={})",
                                customer.getId(), referrer.getId(), normalised);
                    }
                });
            }
        }

        customerRepo.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public ReferralInfoDto getReferralInfo(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        // Count how many successful referees this customer has (reward already credited).
        long referralCount = customerRepo.countReferredAndRewarded(customerId);

        // Points earned from referrals = referralCount * referrerBonus.
        int pointsFromReferrals = (int) (referralCount * referrerBonus);

        return ReferralInfoDto.builder()
                .referralCode(customer.getReferralCode())
                .referralCount(referralCount)
                .pointsFromReferrals(pointsFromReferrals)
                .refereeBonus(refereeBonus)
                .referrerBonus(referrerBonus)
                .shareUrl(shareBaseUrl + customer.getReferralCode())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> validateCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        return customerRepo.findByReferralCode(code.trim().toUpperCase())
                .map(Customer::getId);
    }

    @Override
    @Transactional
    public void creditReferralBonusIfEligible(Long refereeCustomerId, Long orderId) {
        Customer referee = customerRepo.findById(refereeCustomerId).orElse(null);
        if (referee == null) return;

        // Only credit once, and only if this customer was referred by someone.
        if (referee.isReferralRewardCredited() || referee.getReferredBy() == null) return;

        // Check this is the referee's first COMPLETED order.
        long completedOrders = loyaltyTransactionRepo.countByCustomerIdAndType(
                refereeCustomerId, LoyaltyTransactionType.EARNED);

        // If they have exactly 1 EARNED transaction it means we just earned from
        // their very first order in LoyaltyEventListener (which fires first).
        // Guard: credit only when completedOrders == 1 to stay idempotent.
        if (completedOrders != 1) return;

        Customer referrer = referee.getReferredBy();

        // --- Credit referee bonus ---
        loyaltyService.adjustPoints(
                refereeCustomerId,
                orderId,
                refereeBonus,
                "Referral sign-up bonus (" + refereeBonus + " pts) for joining via code " + referrer.getReferralCode()
        );

        // --- Credit referrer bonus ---
        loyaltyService.adjustPoints(
                referrer.getId(),
                orderId,
                referrerBonus,
                "Referral reward (" + referrerBonus + " pts) — friend completed first order #" + orderId
        );

        // Mark as credited to prevent double-crediting.
        referee.setReferralRewardCredited(true);
        customerRepo.save(referee);

        log.info("Referral bonuses credited: referee #{} +{} pts, referrer #{} +{} pts for order #{}",
                refereeCustomerId, refereeBonus, referrer.getId(), referrerBonus, orderId);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String code = randomCode();
            if (customerRepo.findByReferralCode(code).isEmpty()) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique referral code after 20 attempts.");
    }

    private static String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
