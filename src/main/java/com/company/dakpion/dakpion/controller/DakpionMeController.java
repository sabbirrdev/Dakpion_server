package com.company.dakpion.dakpion.controller;
import com.company.dakpion.base.BasePageableRequest;
import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.dakpion.service.DakpionProfileService;
import com.company.dakpion.sys.dto.AppUserDto;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.company.dakpion.base.BaseConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "DakPion User Profile & Inbox", description = "Current authenticated user profile and their received/sent letters")
@SecurityRequirement(name = "Bearer Authentication")
public class DakpionMeController {
    private final BaseUtils baseUtils;
    private final DakpionProfileService dakpionProfileService;
    private final AuthTokenUtils authTokenUtils;

    @GetMapping
    @Operation(summary = "Get current user profile", description = "Returns user profile attributes based on current JWT token")
    public BaseResponse getCurrentUser(HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(dakpionProfileService.getProfile(authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @org.springframework.web.bind.annotation.PutMapping
    @Operation(summary = "Update current user profile", description = "Updates user profile attributes")
    public BaseResponse updateCurrentUser(@RequestBody AppUserDto appUserDto, HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(dakpionProfileService.updateProfile(appUserDto, authTokenUtils.getUserIdFromRequest(request)),UPDATE_MESSAGE,UPDATE_MESSAGE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping({"/verify-phone", "/phone/verify"})
    @Operation(summary = "Verify phone number for current user", description = "Verifies submitted OTP code and marks phone as verified for current user")
    public BaseResponse verifyPhone(@Valid @RequestBody OtpVerifyRequestDto otpVerifyRequestDto, HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(dakpionProfileService.verifyPhone(otpVerifyRequestDto, authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping(value = "/letters/received")
    @Operation(summary = "Get received letters", description = "Retrieves all letters addressed to the current user (by phone OR recipientUserId)")
    public BaseResponse getReceivedLetters(@RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try{
            if (basePageableRequest == null) {
                basePageableRequest = new BasePageableRequest();
            }
            if (basePageableRequest.getPage() == null) basePageableRequest.setPage(0);
            if (basePageableRequest.getSize() == null) basePageableRequest.setSize(20);
            return  baseUtils.generateSuccessResponse(dakpionProfileService.getReceivedLetters(basePageableRequest,authTokenUtils.getUserIdFromRequest(request) ),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping(value = "/letters/sent")
    @Operation(summary = "Get sent letters", description = "Retrieves all letters written by the current user using pepper-hashed phone matching")
    public BaseResponse getSentLetters(@RequestBody(required = false) BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try{
            if (basePageableRequest == null) {
                basePageableRequest = new BasePageableRequest();
            }
            if (basePageableRequest.getPage() == null) basePageableRequest.setPage(0);
            if (basePageableRequest.getSize() == null) basePageableRequest.setSize(20);
            return  baseUtils.generateSuccessResponse(dakpionProfileService.getSentLetters(basePageableRequest,authTokenUtils.getUserIdFromRequest(request) ),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

}
