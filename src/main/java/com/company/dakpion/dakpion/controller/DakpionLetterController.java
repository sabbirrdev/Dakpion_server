package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.ComposeLetterRequestDto;
import com.company.dakpion.dakpion.service.DakpionLetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping("/api/v1/letters")
@RequiredArgsConstructor
@Tag(name = "DakPion Letters", description = "Letter composition, public viewing, and envelope opening endpoints")
public class DakpionLetterController {

    private final DakpionLetterService letterService;
    private final BaseUtils baseUtils;

    @PostMapping("/compose")
    @Operation(summary = "Compose and submit a letter", description = "Submits a letter with verified OTP, phone hashing with pepper, XSS sanitization, and bad-words filter")
    public BaseResponse composeLetter(
            @Valid @RequestBody ComposeLetterRequestDto request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        try {
            return baseUtils.generateSuccessResponse(letterService.composeLetter(request, idempotencyKey), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get letter by UUID", description = "Public view of a letter for envelope opening; never exposes raw sender phone number")
    public BaseResponse getLetterById(@PathVariable("id") UUID id) {
        try {
            return baseUtils.generateSuccessResponse(letterService.getLetterById(id), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{id}/open")
    @Operation(summary = "Mark letter as opened", description = "Idempotently updates letter status to OPENED and sets openedAt timestamp")
    public BaseResponse markOpened(@PathVariable("id") UUID id) {
        try {
            return baseUtils.generateSuccessResponse(letterService.markOpened(id), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
