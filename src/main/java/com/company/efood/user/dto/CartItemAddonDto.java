package com.company.efood.user.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.user.entity.CartItem;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartItemAddonDto extends BaseDto {
    private Long cartItemId; // The cart item this addon belongs to
    private Long productAddonId; // Reference to the original ProductAddon ID
    private String name; // Name of the addon at the time it was added to cart
    private BigDecimal price;
}
