package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.user.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "REVIEW")
@Entity
public class Review extends BaseEntity {

    @JoinColumn(name = "SHOP_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @Column(name = "RATING", nullable = false)
    private double rating;

    @Column(name = "COMMENT",columnDefinition = "text")
    private String comment;

    @Column(name = "REVIEW_DATE")
    private LocalDateTime reviewDate = LocalDateTime.now();
}
