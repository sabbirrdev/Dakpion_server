package com.company.efood.user.dto;

import com.company.efood.user.entity.LoyaltyTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyTransactionDto {
    private Long id;
    private Long orderId;
    private LoyaltyTransactionType type;
    private Integer points;
    private String description;
    private LocalDateTime entryDate;
}
