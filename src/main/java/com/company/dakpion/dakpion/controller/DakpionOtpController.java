package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.OtpRequestDto;
import com.company.dakpion.dakpion.dto.OtpResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.dakpion.dto.OtpVerifyResponseDto;
import com.company.dakpion.dakpion.dto.ServiceResult;
import com.company.dakpion.dakpion.service.DakpionOtpService;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Tag(name = "DakPion OTP", description = "One-Time Password authentication and rate-limited phone verification")
public class DakpionOtpController {

    private final DakpionOtpService otpService;
    private final AuthTokenUtils authTokenUtils;
    private final BaseUtils baseUtils;

    @PostMapping("/request")
    @Operation(summary = "Request OTP challenge", description = "Initiates OTP challenge via SMS with sliding-window Redis rate limits (1/min, max 5/hr)")
    public BaseResponse requestOtp(HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(otpService.requestOtp(authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP code", description = "Validates the submitted 4-digit code and returns verification authorization token")
    public BaseResponse verifyOtp(@Valid @RequestBody OtpVerifyRequestDto request) {
        try{
            return  baseUtils.generateSuccessResponse(otpService.verifyOtp(request),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }
}
