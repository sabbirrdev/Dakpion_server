package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeliveryFeePolicyDto extends BaseDto {
    private String name;
    private Double baseFee;
    private Double perKmRate;
    private Double minDistanceKm;
    private Double maxDistanceKm;
    private Boolean isActive;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String description;
}
