package com.company.efood.sys.services;

import com.company.efood.sys.dto.RideBookingDto;
import com.company.efood.sys.dto.RideEstimateRequestDto;
import com.company.efood.sys.dto.RideEstimateResponseDto;
import com.company.efood.sys.entity.RideRequest;

import java.util.List;

public interface RideService {
    RideEstimateResponseDto estimateFare(RideEstimateRequestDto requestDto);
    RideRequest createRideRequest(RideBookingDto bookingDto, Long userId);
    List<RideRequest> getCustomerRides(Long userId);
    RideRequest cancelRide(Long rideId, Long userId);
    
    // Rider specific methods
    List<RideRequest> getAvailableRides(String vehicleType);
    RideRequest acceptRide(Long rideId, Long riderId, Long userId);
    RideRequest updateRideStatus(Long rideId, String status, Long riderId, Long userId);
    List<RideRequest> getRiderRides(Long riderId);
}
