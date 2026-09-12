package com.company.dakpion.dakpion.mapper;

import com.company.dakpion.dakpion.dto.*;
import com.company.dakpion.dakpion.entity.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DakpionMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    // Theme Mapping
    @Mapping(target = "name", expression = "java(toLocalizedText(entity.getNameEn(), entity.getNameBn()))")
    @Mapping(target = "description", expression = "java(toLocalizedText(entity.getDescriptionEn(), entity.getDescriptionBn()))")
    @Mapping(target = "occasion", expression = "java(toLocalizedOccasion(entity.getOccasionEn(), entity.getOccasionBn()))")
    @Mapping(target = "palette", expression = "java(toThemePalette(entity.getPalette()))")
    @Mapping(target = "price", expression = "java(entity.getPrice() != null ? entity.getPrice().doubleValue() : 0.0)")
    public abstract ThemeDefinitionDto toThemeDto(DakpionThemeEntity entity);

    public abstract List<ThemeDefinitionDto> toThemeDtoList(List<DakpionThemeEntity> entities);

    // Audio Track Mapping
    @Mapping(target = "name", expression = "java(toLocalizedText(entity.getNameEn(), entity.getNameBn()))")
    @Mapping(target = "category", expression = "java(toLocalizedText(entity.getCategoryEn(), entity.getCategoryBn()))")
    public abstract AudioTrackDto toAudioTrackDto(DakpionAudioTrackEntity entity);

    public abstract List<AudioTrackDto> toAudioTrackDtoList(List<DakpionAudioTrackEntity> entities);

    // Delivery Option Mapping
    @Mapping(target = "name", expression = "java(toLocalizedText(entity.getNameEn(), entity.getNameBn()))")
    @Mapping(target = "description", expression = "java(toLocalizedText(entity.getDescriptionEn(), entity.getDescriptionBn()))")
    @Mapping(target = "etaLabel", expression = "java(toLocalizedText(entity.getEtaLabelEn(), entity.getEtaLabelBn()))")
    @Mapping(target = "price", expression = "java(entity.getPrice() != null ? entity.getPrice().doubleValue() : 0.0)")
    public abstract DeliveryOptionDto toDeliveryOptionDto(DakpionDeliveryOptionEntity entity);

    public abstract List<DeliveryOptionDto> toDeliveryOptionDtoList(List<DakpionDeliveryOptionEntity> entities);

    // Pricing Plan Mapping
    @Mapping(target = "name", expression = "java(toLocalizedText(entity.getNameEn(), entity.getNameBn()))")
    @Mapping(target = "tagline", expression = "java(toLocalizedText(entity.getTaglineEn(), entity.getTaglineBn()))")
    @Mapping(target = "billingUnit", expression = "java(toLocalizedText(entity.getBillingUnitEn(), entity.getBillingUnitBn()))")
    @Mapping(target = "features", expression = "java(toPricingFeatures(entity.getFeatures()))")
    @Mapping(target = "price", expression = "java(entity.getPrice() != null ? entity.getPrice().doubleValue() : 0.0)")
    public abstract PricingPlanDto toPricingPlanDto(DakpionPricingPlanEntity entity);

    public abstract List<PricingPlanDto> toPricingPlanDtoList(List<DakpionPricingPlanEntity> entities);

    // Testimonial Mapping
    @Mapping(target = "quote", expression = "java(toLocalizedText(entity.getQuoteEn(), entity.getQuoteBn()))")
    @Mapping(target = "city", expression = "java(toLocalizedText(entity.getCityEn(), entity.getCityBn()))")
    public abstract TestimonialDto toTestimonialDto(DakpionTestimonialEntity entity);

    public abstract List<TestimonialDto> toTestimonialDtoList(List<DakpionTestimonialEntity> entities);

    // FAQ Mapping
    @Mapping(target = "question", expression = "java(toLocalizedText(entity.getQuestionEn(), entity.getQuestionBn()))")
    @Mapping(target = "answer", expression = "java(toLocalizedText(entity.getAnswerEn(), entity.getAnswerBn()))")
    public abstract FaqItemDto toFaqItemDto(DakpionFaqEntity entity);

    public abstract List<FaqItemDto> toFaqItemDtoList(List<DakpionFaqEntity> entities);

    // Font Mapping
    public abstract FontDto toFontDto(DakpionFontEntity entity);

    public abstract List<FontDto> toFontDtoList(List<DakpionFontEntity> entities);

    // Letter Mapping
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "shippingAddress", expression = "java(toShippingAddress(entity.getShippingAddress()))")
    @Mapping(target = "createdAt", expression = "java(formatDateTime(entity.getCreatedAt()))")
    @Mapping(target = "openedAt", expression = "java(formatDateTime(entity.getOpenedAt()))")
    public abstract LetterResponseDto toLetterDto(DakpionLetterEntity entity);

    public abstract List<LetterResponseDto> toLetterDtoList(List<DakpionLetterEntity> entities);

    // Helper Methods
    protected LocalizedTextDto toLocalizedText(String en, String bn) {
        if (en == null && bn == null) return null;
        return LocalizedTextDto.builder()
                .en(en != null ? en : "")
                .bn(bn != null ? bn : "")
                .build();
    }

    protected LocalizedTextDto toLocalizedOccasion(String en, String bn) {
        if (en == null && bn == null) return null;
        return LocalizedTextDto.builder()
                .en(en != null ? en : "")
                .bn(bn != null ? bn : "")
                .build();
    }

    protected ThemePaletteDto toThemePalette(String json) {
        if (json == null || json.isBlank()) return ThemePaletteDto.builder().build();
        try {
            return objectMapper.readValue(json, ThemePaletteDto.class);
        } catch (Exception e) {
            return ThemePaletteDto.builder().build();
        }
    }

    protected List<LocalizedTextDto> toPricingFeatures(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<LocalizedTextDto>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    protected ShippingAddressDto toShippingAddress(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, ShippingAddressDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    protected String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(ISO_FORMATTER);
    }
}
