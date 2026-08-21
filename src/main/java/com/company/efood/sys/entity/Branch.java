package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "BRANCH")
public class Branch extends BaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private Address address;

    @Column(name = "IS_OPEN", nullable = false)
    private Boolean isOpen = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productList;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpeningHour> openingHours;

}
