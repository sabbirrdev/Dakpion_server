package com.company.efood.sys.dto;

import lombok.Data;

@Data
public class RideEstimateResponseDto {
    private String vehicleType;
    private Double distanceKm;
    private Double baseFare;
    private Double perKmRate;
    private Double distanceFare;
    private Double minimumFare;
    private Double totalFare;
    private Double platformCommissionPercent;
    private Double commissionAmount;
    private Double riderBonus;
    private Double riderEarnings;
}
