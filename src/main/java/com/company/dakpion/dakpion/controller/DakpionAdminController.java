package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.dto.*;
import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.service.DakpionAdminService;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping("/api/v1/admin/letters")
@RequiredArgsConstructor
@Tag(name = "DakPion Admin", description = "JWT-protected admin moderation, physical courier dispatch, and print PDF generation")
@SecurityRequirement(name = "Bearer Authentication")
public class DakpionAdminController {

    private final DakpionAdminService adminService;
    private final AuthTokenUtils authTokenUtils;
    private final DakpionDeliveryEventRepo deliveryEventRepo;
    private final DakpionLetterRepo letterRepo;
    private final BaseUtils baseUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'MODERATOR')")
    @Operation(summary = "Moderation Queue", description = "Retrieves paginated letters filtered by moderation status (e.g. PENDING)")
    public BaseResponse getModerationQueue(
            @RequestParam(name = "status", required = false) ModerationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            return baseUtils.generateSuccessResponse(adminService.getModerationQueue(status, pageable), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{id}/moderate")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'MODERATOR')")
    @Operation(summary = "Moderate Letter", description = "Approves or rejects a submitted letter. On approval for SMS Speed Post, dispatches alert SMS.")
    public BaseResponse moderateLetter(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AdminModerationRequestDto request,
            HttpServletRequest httpRequest) {
        try {
            Long adminUserId = authTokenUtils.getUserIdFromRequest(httpRequest);
            return baseUtils.generateSuccessResponse(adminService.moderateLetter(id, request, adminUserId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{id}/courier-book")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'MODERATOR')")
    @Operation(summary = "Book Courier Dispatch", description = "Books parcel shipment with Pathao/SteadFast courier for physical delivery letters")
    public BaseResponse bookCourier(
            @PathVariable("id") UUID id,
            @RequestBody(required = false) AdminCourierBookingRequestDto request) {
        try {
            AdminCourierBookingRequestDto bookingRequest = request != null ? request : new AdminCourierBookingRequestDto();
            return baseUtils.generateSuccessResponse(adminService.bookCourier(id, bookingRequest), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/{id}/print-pdf")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'MODERATOR')")
    @Operation(summary = "Generate Vintage Print PDF", description = "Generates A4 vintage parchment PDF ready for physical print fulfillment and wax-sealing")
    public ResponseEntity<byte[]> printPdf(@PathVariable("id") UUID id) {
        byte[] pdfBytes = adminService.printVintagePdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "dakpion-letter-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @PostMapping("/{id}/delivery-events")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    @Operation(summary = "Add Delivery Event", description = "Appends a physical tracking milestone to a PHYSICAL letter's delivery timeline")
    public BaseResponse addDeliveryEvent(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AddDeliveryEventRequestDto request,
            HttpServletRequest httpRequest) {
        try {
            Long adminUserId = authTokenUtils.getUserIdFromRequest(httpRequest);

            DakpionLetterEntity letter = letterRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Letter not found: " + id));

            DakpionDeliveryEventEntity event = DakpionDeliveryEventEntity.builder()
                    .letterId(letter.getId())
                    .status(request.getStatus())
                    .note(request.getNote())
                    .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
                    .createdBy("ADMIN:" + adminUserId)
                    .build();
            deliveryEventRepo.save(event);

            return baseUtils.generateSuccessResponse("Delivery event added.", PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
