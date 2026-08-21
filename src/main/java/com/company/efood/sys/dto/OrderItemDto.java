package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.user.dto.BasePurchasableItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItemDto extends BasePurchasableItemDto {
    private Long orderId;
    private Long customerId;
    private Long branchId;

}
