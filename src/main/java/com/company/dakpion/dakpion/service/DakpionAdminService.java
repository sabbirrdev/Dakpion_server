package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.dto.AdminModerationRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DakpionAdminService {
    Page<LetterResponseDto> getModerationQueue(ModerationStatus status, Pageable pageable);
    LetterResponseDto moderateLetter(UUID id, AdminModerationRequestDto request, Long adminUserId);
    AdminCourierBookingResponseDto bookCourier(UUID id, AdminCourierBookingRequestDto request);
    byte[] printVintagePdf(UUID id);
}
