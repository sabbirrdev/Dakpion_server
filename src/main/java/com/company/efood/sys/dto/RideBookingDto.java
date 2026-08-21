package com.company.efood.sys.dto;

import lombok.Data;

@Data
public class RideBookingDto {
    private Long id;
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLon;
    private String dropAddress;
    private Double dropLat;
    private Double dropLon;
    private String vehicleType;
    private Double distanceKm;
    private Double totalFare;
    private String paymentMethod = "COD";
    private String status;
}
