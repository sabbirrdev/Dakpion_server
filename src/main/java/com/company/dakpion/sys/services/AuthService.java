package com.company.dakpion.sys.services;
import com.company.dakpion.dakpion.dto.OtpResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.sys.dto.AuthSessionResponseDto;
import com.company.dakpion.sys.dto.ForgotPasswordRequestDto;
import com.company.dakpion.sys.dto.RefreshTokenRequestDto;
import com.company.dakpion.sys.dto.ResetPasswordRequestDto;
import com.company.dakpion.sys.model.AuthResponseModel;
import com.company.dakpion.sys.model.LoginRequestModel;
import com.company.dakpion.sys.model.RegisterRequestModel;

public interface AuthService {
    AuthResponseModel signIn(LoginRequestModel loginRequestModel);
    AuthResponseModel signUp(RegisterRequestModel registerRequestModel);
    OtpResponseDto requestAuthOtp(Long userId);
    AuthSessionResponseDto verifyAuthOtp(OtpVerifyRequestDto requestDto);
    OtpResponseDto requestForgotPassword(ForgotPasswordRequestDto requestDto);
    AuthSessionResponseDto resetPassword(ResetPasswordRequestDto requestDto);
    AuthSessionResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
    Boolean logout(Long userId);
}
