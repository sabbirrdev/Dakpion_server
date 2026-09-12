package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Short link resolver. Resolves /l/{shortCode} to the letter UUID (or full letter page).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DakpionShortLinkController {

    private final DakpionLetterRepo letterRepo;
    private final com.company.dakpion.base.BaseUtils baseUtils;

    /**
     * GET /l/{shortCode}
     * Redirects to the letter view page using the short code.
     */
    @GetMapping("/l/{shortCode}")
    public ResponseEntity<Void> resolveShortLink(@PathVariable String shortCode) {
        DakpionLetterEntity letter = letterRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short link not found."));

        // Redirect to the frontend letter reading page
        String redirectUrl = "/api/v1/letters/" + letter.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * GET /api/v1/letters/short/{shortCode}
     * Returns the letter UUID for clients that need to resolve without redirects.
     */
    @GetMapping("/api/v1/letters/short/{shortCode}")
    public com.company.dakpion.base.BaseResponse resolveShortCode(@PathVariable String shortCode) {
        try {
            DakpionLetterEntity letter = letterRepo.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Short link not found."));
            return baseUtils.generateSuccessResponse(new ResolveResponse(letter.getId().toString(), shortCode), com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE, com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    record ResolveResponse(String letterId, String shortCode) {}
}
