package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.dto.ComposeLetterRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;

import java.util.UUID;

public interface DakpionLetterService {
    LetterResponseDto composeLetter(ComposeLetterRequestDto request, String idempotencyKey);
    LetterResponseDto getLetterById(UUID id);
    LetterResponseDto markOpened(UUID id);
}
