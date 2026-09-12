package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.dto.OtpResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.dakpion.dto.OtpVerifyResponseDto;

public interface DakpionOtpService {
    OtpResponseDto requestOtp(Long userId);
    OtpVerifyResponseDto verifyOtp(OtpVerifyRequestDto request);
    boolean isPhoneVerified(String phone, String verificationToken, String requestId);
    String getVerifiedPhone(String verificationToken, String requestId);
}
