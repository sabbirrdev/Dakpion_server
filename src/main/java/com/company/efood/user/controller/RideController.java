package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.RideBookingDto;
import com.company.efood.sys.dto.RideEstimateRequestDto;
import com.company.efood.sys.services.RideService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "ride")
@AllArgsConstructor
public class RideController {

    private final RideService rideService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping("/estimate-fare")
    public BaseResponse estimateFare(@RequestBody RideEstimateRequestDto requestDto) {
        try {
            return baseUtils.generateSuccessResponse(rideService.estimateFare(requestDto), "Ride fare estimated successfully", "রাইডের ভাড়া হিসাব করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/request")
    public BaseResponse requestRide(@RequestBody RideBookingDto bookingDto, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(rideService.createRideRequest(bookingDto, userId), "Ride requested successfully", "রাইড অনুরোধ সম্পন্ন হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/my")
    public BaseResponse getMyRides(HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(rideService.getCustomerRides(userId), "Rides fetched successfully", "রাইডের তালিকা প্রদান করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/cancel/{id}")
    public BaseResponse cancelRide(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(rideService.cancelRide(id, userId), "Ride cancelled", "রাইড বাতিল করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
