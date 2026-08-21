package com.company.efood.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralInfoDto {

    /** This customer's own shareable referral code. */
    private String referralCode;

    /** How many customers this user has successfully referred (with completed order). */
    private long referralCount;

    /** Points earned from referrals so far. */
    private int pointsFromReferrals;

    /** Points that the referee (new user) receives on first completed order. */
    private int refereeBonus;

    /** Points that the referrer receives when the referee completes their first order. */
    private int referrerBonus;

    /** Deep-link share URL. */
    private String shareUrl;
}
