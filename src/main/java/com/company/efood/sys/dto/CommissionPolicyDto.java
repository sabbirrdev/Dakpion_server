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
public class CommissionPolicyDto extends BaseDto {
    private String name;
    private Double percentage;
    private Double fixedAmount;
    private Boolean isDynamic;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
