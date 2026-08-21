package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRODUCT_IMAGE")
@Entity
public class ProductImage extends BaseEntity {
    @Column(name = "IMAGE_URL")
    private  String imgUrl;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}
