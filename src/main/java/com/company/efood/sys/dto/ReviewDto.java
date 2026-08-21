package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.sys.entity.Product;
import com.company.efood.sys.entity.Shop;
import com.company.efood.user.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewDto extends BaseDto {
    private double rating;
    private Integer productId;
    private String productName;
    private Integer shopId;
    private String  shopName;
    private Integer customerId;
    private String customerName;
    private String comment;
    private LocalDateTime reviewDate;
}
