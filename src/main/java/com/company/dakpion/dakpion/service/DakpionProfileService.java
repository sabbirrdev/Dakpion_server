package com.company.dakpion.dakpion.service;

import com.company.dakpion.base.BasePageableRequest;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.dto.OtpVerifyRequestDto;
import com.company.dakpion.sys.dto.AppUserDto;
import org.springframework.data.domain.Page;

public interface DakpionProfileService {
    AppUserDto getProfile(Long userId);
    Page<LetterResponseDto> getSentLetters(BasePageableRequest pageableBodyRequest,Long userId);
    Page<LetterResponseDto> getReceivedLetters(BasePageableRequest pageableBodyRequest,Long userId);
    AppUserDto verifyPhone(OtpVerifyRequestDto otpVerifyRequestDto,Long userId);
    AppUserDto updateProfile(AppUserDto appUserDto,Long userId);
}
