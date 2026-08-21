package com.company.efood.seller.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.entity.Address;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.entity.Branch;
import com.company.efood.sys.entity.Shop;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table(name = "SYS_EMPLOYEE")
@Entity
@EqualsAndHashCode(callSuper = true)
public class Employee extends BaseEntity {
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phone;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", unique = true, nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;
}
