package com.company.efood.user.dto;
import com.company.efood.user.entity.CartItemAddon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartItemDto extends BasePurchasableItemDto {
    private Long cartId;
    private String cartKey;
    private Long customerId;
    private Long branchId;
}

