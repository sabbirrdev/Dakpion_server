package com.company.efood.user.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "LOYALTY_ACCOUNT", indexes = {
        @Index(name = "idx_loyalty_account_customer", columnList = "CUSTOMER_ID", unique = true)
})
@NoArgsConstructor
public class LoyaltyAccount extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "CURRENT_POINTS", nullable = false)
    private Integer currentPoints = 0;

    @Column(name = "LIFETIME_POINTS_EARNED", nullable = false)
    private Integer lifetimePointsEarned = 0;

    @Version
    @Column(name = "VERSION")
    private Long version;

    public LoyaltyAccount(Customer customer) {
        this.customer = customer;
        this.currentPoints = 0;
        this.lifetimePointsEarned = 0;
    }
}
