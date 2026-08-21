package com.company.efood.seller.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.sys.entity.Branch;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OpeningHourDto extends BaseDto {
    private Integer branchId;
    private DayOfWeek day; // java.time.DayOfWeek (MONDAY to SUNDAY)
    private LocalTime openTime;
    private LocalTime closeTime;
}
