package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.dto.LetterResponseDto;

import java.util.Optional;

public interface IdempotencyService {
    Optional<LetterResponseDto> getCachedResponse(String idempotencyKey);
    void saveResponse(String idempotencyKey, LetterResponseDto responseDto);
}
