package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.*;
import com.company.dakpion.dakpion.service.DakpionCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.company.dakpion.base.BaseConstants.*;

@RestController
@RequestMapping("/api/v1/admin/catalog")
@RequiredArgsConstructor
@Tag(name = "DakPion Admin CMS", description = "Admin CRUD operations for themes, audio tracks, delivery options, pricing, and fonts")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'DEVELOPER')")
public class DakpionAdminCatalogController {

    private final DakpionCatalogService catalogService;
    private final BaseUtils baseUtils;

    // ── Theme Management ──────────────────────────────────────────────────
    @PostMapping("/themes")
    @Operation(summary = "Create or update theme")
    public BaseResponse saveTheme(@Valid @RequestBody ThemeDefinitionDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveTheme(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/themes/{id}")
    @Operation(summary = "Update theme by ID")
    public BaseResponse updateTheme(@PathVariable("id") String id, @Valid @RequestBody ThemeDefinitionDto dto) {
        try{
            dto.setId(id);
            return  baseUtils.generateSuccessResponse(catalogService.saveTheme(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @DeleteMapping("/themes/{id}")
    @Operation(summary = "Delete theme by ID")
    public BaseResponse deleteTheme(@PathVariable("id") String id) {
        try{
            catalogService.deleteTheme(id);
            return  baseUtils.generateSuccessResponse("Theme deleted successfully",PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ── Audio Track Management ────────────────────────────────────────────
    @PostMapping("/audio-tracks")
    @Operation(summary = "Create or update audio track")
    public BaseResponse saveAudioTrack(@Valid @RequestBody AudioTrackDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveAudioTrack(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/audio-tracks/{id}")
    @Operation(summary = "Update audio track by ID")
    public BaseResponse updateAudioTrack(@PathVariable("id") String id, @Valid @RequestBody AudioTrackDto dto) {
        try{
            dto.setId(id);
            return  baseUtils.generateSuccessResponse(catalogService.saveAudioTrack(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @DeleteMapping("/audio-tracks/{id}")
    @Operation(summary = "Delete audio track by ID")
    public BaseResponse deleteAudioTrack(@PathVariable("id") String id) {
        try{
            catalogService.deleteAudioTrack(id);
            return  baseUtils.generateSuccessResponse("Audio track deleted successfully",PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ── Delivery Option Management ────────────────────────────────────────
    @PostMapping("/delivery-options")
    @Operation(summary = "Create or update delivery option")
    public BaseResponse saveDeliveryOption(@Valid @RequestBody DeliveryOptionDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveDeliveryOption(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/delivery-options/{type}")
    @Operation(summary = "Update delivery option by type")
    public BaseResponse updateDeliveryOption(@PathVariable("type") String type, @Valid @RequestBody DeliveryOptionDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveDeliveryOption(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @DeleteMapping("/delivery-options/{type}")
    @Operation(summary = "Delete delivery option by type")
    public BaseResponse deleteDeliveryOption(@PathVariable("type") String type) {
        try{
            catalogService.deleteDeliveryOption(type);
            return  baseUtils.generateSuccessResponse("Delivery option deleted successfully",PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ── Pricing Plan Management ───────────────────────────────────────────
    @PostMapping("/pricing-plans")
    @Operation(summary = "Create or update pricing plan")
    public BaseResponse savePricingPlan(@Valid @RequestBody PricingPlanDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.savePricingPlan(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/pricing-plans/{id}")
    @Operation(summary = "Update pricing plan by ID")
    public BaseResponse updatePricingPlan(@PathVariable("id") String id, @Valid @RequestBody PricingPlanDto dto) {
        try{
            dto.setId(id);
            return  baseUtils.generateSuccessResponse(catalogService.savePricingPlan(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }

    @DeleteMapping("/pricing-plans/{id}")
    @Operation(summary = "Delete pricing plan by ID")
    public BaseResponse deletePricingPlan(@PathVariable("id") String id) {
        try{
            catalogService.deletePricingPlan(id);
            return  baseUtils.generateSuccessResponse("Pricing plan deleted successfully",DELETE_MESSAGE,DELETE_MESSAGE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    // ── Font & Typography Management ──────────────────────────────────────
    @PostMapping("/fonts")
    @Operation(summary = "Create or update font")
    public BaseResponse saveFont(@Valid @RequestBody FontDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveFont(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/fonts/{id}")
    @Operation(summary = "Update font by ID")
    public BaseResponse updateFont(@PathVariable("id") String id, @Valid @RequestBody FontDto dto) {
        try{
            dto.setId(id);
            return  baseUtils.generateSuccessResponse(catalogService.saveFont(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }

    @DeleteMapping("/fonts/{id}")
    @Operation(summary = "Delete font by ID")
    public BaseResponse deleteFont(@PathVariable("id") String id) {
        try{
            catalogService.deleteFont(id);
            return  baseUtils.generateSuccessResponse("Font deleted successfully",PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }

    // ── FAQ Management ───────────────────────────────────────────────────
    @PostMapping("/faq")
    @Operation(summary = "Create or update FAQ item")
    public BaseResponse saveFaq(@Valid @RequestBody FaqItemDto dto) {
        try{
            return  baseUtils.generateSuccessResponse(catalogService.saveFaq(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping("/faq/{id}")
    @Operation(summary = "Update FAQ item by ID")
    public BaseResponse updateFaq( @PathVariable("id") String id, @Valid @RequestBody FaqItemDto dto) {
        try{
            dto.setId(id);
            return  baseUtils.generateSuccessResponse(catalogService.saveFaq(dto),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }

    @DeleteMapping("/faq/{id}")
    @Operation(summary = "Delete FAQ item by ID")
    public BaseResponse deleteFaq(@PathVariable("id") String id) {
        try{
            catalogService.deleteFaq(id);
            return  baseUtils.generateSuccessResponse("FAQ item deleted successfully",PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }

    }
}
