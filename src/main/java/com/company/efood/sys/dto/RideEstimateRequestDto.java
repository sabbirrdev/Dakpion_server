package com.company.efood.sys.dto;

import lombok.Data;

@Data
public class RideEstimateRequestDto {
    private Double pickupLat;
    private Double pickupLon;
    private Double dropLat;
    private Double dropLon;
    private String vehicleType; // BIKE, BICYCLE, CAR, CNG, COVERED_VAN
    private Double distanceKm; // optional user input or computed
}
