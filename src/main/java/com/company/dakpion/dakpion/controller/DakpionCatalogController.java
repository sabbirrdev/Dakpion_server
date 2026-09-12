package com.company.dakpion.dakpion.controller;
import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.service.DakpionCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "DakPion Catalog", description = "Public catalog endpoints for themes, audio tracks, delivery options, pricing, and FAQ")
public class DakpionCatalogController {

    private final DakpionCatalogService catalogService;
    private final BaseUtils baseUtils;

    @GetMapping("/themes")
    @Operation(summary = "List all letter themes", description = "Retrieves all active vintage and modern stationery themes")
    public BaseResponse getThemes() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getThemes(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/audio-tracks")
    @Operation(summary = "List all background audio tracks", description = "Retrieves ambient soundscapes and music tracks")
    public BaseResponse getAudioTracks() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getAudioTracks(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/delivery-options")
    @Operation(summary = "List delivery options & pricing", description = "Retrieves available delivery modes (Digital link, SMS speed post, Physical courier)")
    public BaseResponse getDeliveryOptions() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getDeliveryOptions(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/pricing-plans")
    @Operation(summary = "Marketing pricing plans", description = "Retrieves pricing plans for letter composition and physical delivery")
    public BaseResponse getPricingPlans() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getPricingPlans(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/testimonials")
    @Operation(summary = "Homepage testimonials", description = "Retrieves user stories and nostalgia feedback")
    public BaseResponse getTestimonials() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getTestimonials(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/faq")
    @Operation(summary = "FAQ entries", description = "Frequently asked questions regarding anonymous letter delivery")
    public BaseResponse getFaq() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getFaq(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/fonts")
    @Operation(summary = "List all active fonts", description = "Retrieves dynamic typography options for Bengali and Latin letter composition")
    public BaseResponse getFonts() {
        try {
            return baseUtils.generateSuccessResponse(catalogService.getFonts(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
