package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.dto.RideBookingDto;
import com.company.efood.sys.dto.RideEstimateRequestDto;
import com.company.efood.sys.dto.RideEstimateResponseDto;
import com.company.efood.sys.entity.RideFarePolicy;
import com.company.efood.sys.entity.RideRequest;
import com.company.efood.sys.repository.RideFarePolicyRepo;
import com.company.efood.sys.repository.RideRequestRepo;
import com.company.efood.sys.services.RideService;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.repository.CustomerRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideFarePolicyRepo farePolicyRepo;
    private final RideRequestRepo rideRequestRepo;
    private final CustomerRepo customerRepo;
    private final BaseUtils baseUtils;

    @Override
    public RideEstimateResponseDto estimateFare(RideEstimateRequestDto req) {
        String vType = req.getVehicleType() != null ? req.getVehicleType().toUpperCase() : "BIKE";
        RideFarePolicy policy = farePolicyRepo.findFirstByVehicleTypeIgnoreCaseAndActiveTrue(vType)
                .orElseGet(() -> getDefaultPolicy(vType));

        double distKm = 1.0;
        if (req.getDistanceKm() != null && req.getDistanceKm() > 0) {
            distKm = req.getDistanceKm();
        } else if (req.getPickupLat() != null && req.getPickupLon() != null && req.getDropLat() != null && req.getDropLon() != null) {
            distKm = calculateDistanceKm(req.getPickupLat(), req.getPickupLon(), req.getDropLat(), req.getDropLon());
        }

        double baseFare = policy.getBaseFare();
        double perKmRate = policy.getPerKmRate();
        double distanceFare = distKm * perKmRate;
        double subtotal = baseFare + distanceFare;
        double totalFare = Math.max(subtotal, policy.getMinimumFare());
        totalFare = Math.round(totalFare * 10.0) / 10.0;

        double commPercent = policy.getPlatformCommissionPercent();
        double commAmount = Math.round((totalFare * (commPercent / 100.0)) * 10.0) / 10.0;
        double riderBonus = policy.getRiderBonusPerRide();
        double riderEarnings = Math.round(((totalFare - commAmount) + riderBonus) * 10.0) / 10.0;

        RideEstimateResponseDto res = new RideEstimateResponseDto();
        res.setVehicleType(vType);
        res.setDistanceKm(Math.round(distKm * 100.0) / 100.0);
        res.setBaseFare(baseFare);
        res.setPerKmRate(perKmRate);
        res.setDistanceFare(Math.round(distanceFare * 10.0) / 10.0);
        res.setMinimumFare(policy.getMinimumFare());
        res.setTotalFare(totalFare);
        res.setPlatformCommissionPercent(commPercent);
        res.setCommissionAmount(commAmount);
        res.setRiderBonus(riderBonus);
        res.setRiderEarnings(riderEarnings);

        return res;
    }

    @Transactional
    @Override
    public RideRequest createRideRequest(RideBookingDto dto, Long userId) {
        Long appUserId = CurrentUserContext.getReferenceId();
        Customer customer = customerRepo.findByAppUserId(appUserId).orElse(null);
        Long customerId = customer != null ? customer.getId() : (appUserId != null ? appUserId : 1L);

        RideEstimateRequestDto estimateReq = new RideEstimateRequestDto();
        estimateReq.setPickupLat(dto.getPickupLat());
        estimateReq.setPickupLon(dto.getPickupLon());
        estimateReq.setDropLat(dto.getDropLat());
        estimateReq.setDropLon(dto.getDropLon());
        estimateReq.setVehicleType(dto.getVehicleType());
        estimateReq.setDistanceKm(dto.getDistanceKm());

        RideEstimateResponseDto fareEst = estimateFare(estimateReq);

        RideRequest req = new RideRequest();
        req.setCustomerId(customerId);
        req.setPickupAddress(dto.getPickupAddress() != null ? dto.getPickupAddress() : "Pickup Location");
        req.setPickupLat(dto.getPickupLat());
        req.setPickupLon(dto.getPickupLon());
        req.setDropAddress(dto.getDropAddress() != null ? dto.getDropAddress() : "Destination Location");
        req.setDropLat(dto.getDropLat());
        req.setDropLon(dto.getDropLon());
        req.setDistanceKm(fareEst.getDistanceKm());
        req.setVehicleType(fareEst.getVehicleType());

        req.setBaseFare(fareEst.getBaseFare());
        req.setDistanceFare(fareEst.getDistanceFare());
        req.setTotalFare(fareEst.getTotalFare());
        req.setCommissionAmount(fareEst.getCommissionAmount());
        req.setRiderEarnings(fareEst.getRiderEarnings());
        req.setRiderBonus(fareEst.getRiderBonus());

        req.setStatus("REQUESTED");
        req.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "COD");
        req.setEntryUser(userId != null ? userId : customerId);
        baseUtils.setEntryUserInfo(req);

        return rideRequestRepo.save(req);
    }

    @Override
    public List<RideRequest> getCustomerRides(Long userId) {
        Long appUserId = CurrentUserContext.getReferenceId();
        Customer customer = customerRepo.findByAppUserId(appUserId).orElse(null);
        Long customerId = customer != null ? customer.getId() : (appUserId != null ? appUserId : 1L);
        return rideRequestRepo.findByCustomerIdOrderByIdDesc(customerId);
    }

    @Transactional
    @Override
    public RideRequest cancelRide(Long rideId, Long userId) {
        RideRequest req = rideRequestRepo.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride request not found: " + rideId));
        req.setStatus("CANCELLED");
        req.setUpdateUser(userId);
        return rideRequestRepo.save(req);
    }

    @Override
    public List<RideRequest> getAvailableRides(String vehicleType) {
        List<RideRequest> list = rideRequestRepo.findByStatusOrderByIdDesc("REQUESTED");
        if (vehicleType != null && !vehicleType.isBlank()) {
            return list.stream()
                    .filter(r -> r.getVehicleType() != null && r.getVehicleType().equalsIgnoreCase(vehicleType))
                    .collect(Collectors.toList());
        }
        return list;
    }

    @Transactional
    @Override
    public RideRequest acceptRide(Long rideId, Long riderId, Long userId) {
        RideRequest req = rideRequestRepo.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride request not found: " + rideId));
        if (!"REQUESTED".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("Ride is no longer available (current status: " + req.getStatus() + ")");
        }
        req.setStatus("ACCEPTED");
        req.setRaiderId(riderId);
        req.setUpdateUser(userId);
        return rideRequestRepo.save(req);
    }

    @Transactional
    @Override
    public RideRequest updateRideStatus(Long rideId, String status, Long riderId, Long userId) {
        RideRequest req = rideRequestRepo.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride request not found: " + rideId));
        req.setStatus(status.toUpperCase());
        req.setUpdateUser(userId);
        return rideRequestRepo.save(req);
    }

    @Override
    public List<RideRequest> getRiderRides(Long riderId) {
        return rideRequestRepo.findByRaiderIdOrderByIdDesc(riderId);
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance > 0.2 ? distance : 1.0;
    }

    private RideFarePolicy getDefaultPolicy(String vehicleType) {
        RideFarePolicy p = new RideFarePolicy();
        p.setVehicleType(vehicleType);
        switch (vehicleType.toUpperCase()) {
            case "BICYCLE":
                p.setBaseFare(20.0);
                p.setPerKmRate(10.0);
                p.setMinimumFare(30.0);
                break;
            case "CNG":
                p.setBaseFare(50.0);
                p.setPerKmRate(20.0);
                p.setMinimumFare(70.0);
                break;
            case "CAR":
                p.setBaseFare(100.0);
                p.setPerKmRate(35.0);
                p.setMinimumFare(150.0);
                break;
            case "COVERED_VAN":
                p.setBaseFare(150.0);
                p.setPerKmRate(45.0);
                p.setMinimumFare(200.0);
                break;
            case "BIKE":
            default:
                p.setBaseFare(30.0);
                p.setPerKmRate(15.0);
                p.setMinimumFare(45.0);
                break;
        }
        p.setPlatformCommissionPercent(10.0);
        p.setRiderBonusPerRide(5.0);
        p.setActive(true);
        return p;
    }
}
