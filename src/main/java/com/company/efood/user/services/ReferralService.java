package com.company.efood.user.services;

import com.company.efood.user.dto.ReferralInfoDto;
import com.company.efood.user.entity.Customer;

public interface ReferralService {

    /**
     * Called at registration time to assign a unique referral code to a newly
     * created Customer and (optionally) record who referred them.
     *
     * @param customer       the newly saved Customer entity (already persisted)
     * @param referralCode   the code typed by the new user during signup (may be null/blank)
     */
    void initReferral(Customer customer, String referralCode);

    /**
     * Returns the referral dashboard data for the currently authenticated customer.
     *
     * @param customerId the Customer.id of the current user
     */
    ReferralInfoDto getReferralInfo(Long customerId);

    /**
     * Validates that a referral code exists and returns the referrer customer id.
     * Returns empty Optional if the code is unknown or already used by this customer.
     *
     * @param code the code to validate
     * @return referrer Customer.id or empty
     */
    java.util.Optional<Long> validateCode(String code);

    /**
     * Credits referral bonuses to both the referee and the referrer once the
     * referee completes their very first order. Idempotent — safe to call multiple
     * times (second call is a no-op).
     *
     * @param refereeCustomerId the customer who just completed an order
     * @param orderId           the completed order's id
     */
    void creditReferralBonusIfEligible(Long refereeCustomerId, Long orderId);
}
