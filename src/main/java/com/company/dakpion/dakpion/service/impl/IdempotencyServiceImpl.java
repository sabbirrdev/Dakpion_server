package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final DakpionLetterRepo letterRepo;
    private final DakpionMapper mapper;
    private final ConcurrentHashMap<String, LetterResponseDto> memoryCache = new ConcurrentHashMap<>();

    @Override
    public Optional<LetterResponseDto> getCachedResponse(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        LetterResponseDto fromMemory = memoryCache.get(idempotencyKey);
        if (fromMemory != null) {
            return Optional.of(fromMemory);
        }

        return letterRepo.findByIdempotencyKey(idempotencyKey)
                .map(mapper::toLetterDto);
    }

    @Override
    public void saveResponse(String idempotencyKey, LetterResponseDto responseDto) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || responseDto == null) {
            return;
        }
        memoryCache.put(idempotencyKey, responseDto);
        if (memoryCache.size() > 1000) {
            memoryCache.clear();
        }
    }
}
