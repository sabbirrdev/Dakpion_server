package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.dto.*;

import java.util.List;

public interface DakpionCatalogService {
    List<ThemeDefinitionDto> getThemes();
    List<AudioTrackDto> getAudioTracks();
    List<DeliveryOptionDto> getDeliveryOptions();
    List<PricingPlanDto> getPricingPlans();
    List<TestimonialDto> getTestimonials();
    List<FaqItemDto> getFaq();
    List<FontDto> getFonts();

    // Admin CMS CRUD Operations
    ThemeDefinitionDto saveTheme(ThemeDefinitionDto dto);
    void deleteTheme(String id);

    AudioTrackDto saveAudioTrack(AudioTrackDto dto);
    void deleteAudioTrack(String id);

    DeliveryOptionDto saveDeliveryOption(DeliveryOptionDto dto);
    void deleteDeliveryOption(String type);

    PricingPlanDto savePricingPlan(PricingPlanDto dto);
    void deletePricingPlan(String id);

    FontDto saveFont(FontDto dto);
    void deleteFont(String id);

    FaqItemDto saveFaq(FaqItemDto dto);
    void deleteFaq(String id);
}
