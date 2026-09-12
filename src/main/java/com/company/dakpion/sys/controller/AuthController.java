package com.company.dakpion.sys.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.OtpRequestDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.sys.dto.ForgotPasswordRequestDto;
import com.company.dakpion.sys.dto.RefreshTokenRequestDto;
import com.company.dakpion.sys.dto.ResetPasswordRequestDto;
import com.company.dakpion.sys.model.AuthResponseModel;
import com.company.dakpion.sys.model.LoginRequestModel;
import com.company.dakpion.sys.model.RegisterRequestModel;

import com.company.dakpion.sys.services.AuthService;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import static com.company.dakpion.base.BaseConstants.*;

@Slf4j
@RestController
@RequestMapping({"/api/auth", "/api/v1/auth"})
@AllArgsConstructor
public class AuthController {
    private final AuthTokenUtils authTokenUtils;
    private final BaseUtils baseUtils;
    private final AuthService authService;

    // ─── Username/Password sign-in ────────────────────────────────────────────

    @PostMapping("/signin")
    public BaseResponse signIn(@Valid @RequestBody LoginRequestModel requestModel) {
        try {

            AuthResponseModel authResponseModel = authService.signIn(requestModel);
            return baseUtils.generateSuccessResponse(authResponseModel,PROCESS_COMPLETE,PROCESS_COMPLETE_BN);

        } catch (InternalAuthenticationServiceException e) {
            return BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            return BaseResponse.builder()
                    .status(false)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Wrong username or password")
                    .messageBn("ভুল ব্যবহারকারীর নাম বা পাসওয়ার্ড")
                    .build();
        }
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    @PostMapping("/signup")
    public BaseResponse registerUser(@Valid @RequestBody RegisterRequestModel requestModel) {
        if (requestModel.getUsername().isEmpty() || requestModel.getPassword().isEmpty()) {
            return baseUtils.generateErrorResponse(new Exception("Username and/or password are empty"));
        }
        try{
            AuthResponseModel authResponseModel =  authService.signUp(requestModel);
            return  baseUtils.generateSuccessResponse(authResponseModel,PROCESS_COMPLETE,PROCESS_COMPLETE_BN);

        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ─── OTP-based Phone Authentication ──────────────────────────────────────

    @PostMapping("/otp/request")
    public BaseResponse requestAuthOtp(HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(authService.requestAuthOtp(authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/otp/verify")
    public BaseResponse verifyAuthOtp(@RequestBody OtpVerifyRequestDto requestDto) {
        try{
            return  baseUtils.generateSuccessResponse(authService.verifyAuthOtp(requestDto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ─── Forgot Password Flow ─────────────────────────────────────────────────

    /**
     * Step 1: Request Password Reset OTP by Phone or Username.
     */
    @PostMapping("/forgot-password/request")
    public BaseResponse requestForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        try{
            return  baseUtils.generateSuccessResponse(authService.requestForgotPassword(requestDto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    /**
     * Step 2: Verify OTP and update user password with BCrypt hash.
     */
    @PostMapping("/forgot-password/verify-and-reset")
    public BaseResponse resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {

        try{
            return  baseUtils.generateSuccessResponse(authService.resetPassword(requestDto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ─── Token Refresh & Logout ───────────────────────────────────────────────

    @PostMapping("/refresh")
    public BaseResponse refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        try{
            return  baseUtils.generateSuccessResponse(authService.refreshToken(refreshTokenRequestDto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/logout")
    public BaseResponse logout(HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(authService.logout(authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }
}
