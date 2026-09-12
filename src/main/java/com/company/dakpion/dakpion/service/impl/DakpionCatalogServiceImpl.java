package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.dakpion.dto.*;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import com.company.dakpion.dakpion.repository.*;
import com.company.dakpion.dakpion.service.DakpionCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DakpionCatalogServiceImpl implements DakpionCatalogService {

    private final DakpionThemeRepo themeRepo;
    private final DakpionAudioTrackRepo audioTrackRepo;
    private final DakpionDeliveryOptionRepo deliveryOptionRepo;
    private final DakpionPricingPlanRepo pricingPlanRepo;
    private final DakpionTestimonialRepo testimonialRepo;
    private final DakpionFaqRepo faqRepo;
    private final DakpionFontRepo fontRepo;
    private final DakpionMapper mapper;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "dakpion_themes", key = "'all'")
    public List<ThemeDefinitionDto> getThemes() {
        log.info("[Catalog] Loading themes from DB");
        return mapper.toThemeDtoList(themeRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_audio_tracks", key = "'all'")
    public List<AudioTrackDto> getAudioTracks() {
        log.info("[Catalog] Loading audio tracks from DB");
        return mapper.toAudioTrackDtoList(audioTrackRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_delivery_options", key = "'all'")
    public List<DeliveryOptionDto> getDeliveryOptions() {
        log.info("[Catalog] Loading delivery options from DB");
        return mapper.toDeliveryOptionDtoList(deliveryOptionRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_pricing_plans", key = "'all'")
    public List<PricingPlanDto> getPricingPlans() {
        log.info("[Catalog] Loading pricing plans from DB");
        return mapper.toPricingPlanDtoList(pricingPlanRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_testimonials", key = "'all'")
    public List<TestimonialDto> getTestimonials() {
        log.info("[Catalog] Loading testimonials from DB");
        return mapper.toTestimonialDtoList(testimonialRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_faq", key = "'all'")
    public List<FaqItemDto> getFaq() {
        log.info("[Catalog] Loading FAQs from DB");
        return mapper.toFaqItemDtoList(faqRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    @Override
    @Cacheable(value = "dakpion_fonts", key = "'all'")
    public List<FontDto> getFonts() {
        log.info("[Catalog] Loading fonts from DB");
        return mapper.toFontDtoList(fontRepo.findAllByActiveTrueOrderByDisplayOrderAsc());
    }

    // ── Admin CMS Mutations ───────────────────────────────────────────────

    @Override
    @Transactional
    public ThemeDefinitionDto saveTheme(ThemeDefinitionDto dto) {
        String id = dto.getId() != null && !dto.getId().isBlank()
                ? dto.getId()
                : "theme_" + System.currentTimeMillis();

        com.company.dakpion.dakpion.entity.DakpionThemeEntity entity = themeRepo.findById(id)
                .orElse(new com.company.dakpion.dakpion.entity.DakpionThemeEntity());

        entity.setId(id);
        entity.setNameEn(dto.getName() != null ? dto.getName().getEn() : id);
        entity.setNameBn(dto.getName() != null ? dto.getName().getBn() : id);
        entity.setDescriptionEn(dto.getDescription() != null ? dto.getDescription().getEn() : "");
        com.company.dakpion.dakpion.constant.ThemeTier tier = com.company.dakpion.dakpion.constant.ThemeTier.FREE;
        if (dto.getTier() != null) {
            try {
                tier = com.company.dakpion.dakpion.constant.ThemeTier.valueOf(dto.getTier().toUpperCase());
            } catch (Exception ignored) {}
        }
        entity.setTier(tier);
        entity.setPrice(dto.getPrice() != null ? java.math.BigDecimal.valueOf(dto.getPrice()) : java.math.BigDecimal.ZERO);
        entity.setPreviewImage(dto.getPreviewImage() != null ? dto.getPreviewImage() : "plain");
        if (dto.getOccasion() != null) {
            entity.setOccasionEn(dto.getOccasion().getEn());
            entity.setOccasionBn(dto.getOccasion().getBn());
        }
        if (dto.getPalette() != null) {
            try {
                entity.setPalette(objectMapper.writeValueAsString(dto.getPalette()));
            } catch (Exception e) {
                log.warn("Failed to serialize theme palette", e);
            }
        }
        entity.setActive(true);

        return mapper.toThemeDto(themeRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteTheme(String id) {
        themeRepo.deleteById(id);
    }

    @Override
    @Transactional
    public AudioTrackDto saveAudioTrack(AudioTrackDto dto) {
        String id = dto.getId() != null && !dto.getId().isBlank()
                ? dto.getId()
                : "audio_" + System.currentTimeMillis();

        com.company.dakpion.dakpion.entity.DakpionAudioTrackEntity entity = audioTrackRepo.findById(id)
                .orElse(new com.company.dakpion.dakpion.entity.DakpionAudioTrackEntity());

        entity.setId(id);
        entity.setNameEn(dto.getName() != null ? dto.getName().getEn() : id);
        entity.setNameBn(dto.getName() != null ? dto.getName().getBn() : id);
        entity.setCategoryEn(dto.getCategory() != null ? dto.getCategory().getEn() : "Ambient");
        entity.setCategoryBn(dto.getCategory() != null ? dto.getCategory().getBn() : "অ্যাম্বিয়েন্ট");
        entity.setSrc(dto.getSrc() != null ? dto.getSrc() : "");
        entity.setDurationSeconds(dto.getDurationSeconds() != null ? dto.getDurationSeconds() : 0);
        entity.setIsPremium(dto.getIsPremium() != null ? dto.getIsPremium() : false);
        entity.setActive(true);

        return mapper.toAudioTrackDto(audioTrackRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteAudioTrack(String id) {
        audioTrackRepo.deleteById(id);
    }

    @Override
    @Transactional
    public DeliveryOptionDto saveDeliveryOption(DeliveryOptionDto dto) {
        com.company.dakpion.dakpion.entity.DakpionDeliveryOptionEntity entity = deliveryOptionRepo.findById(dto.getType())
                .orElse(new com.company.dakpion.dakpion.entity.DakpionDeliveryOptionEntity());

        entity.setType(dto.getType());
        entity.setNameEn(dto.getName() != null ? dto.getName().getEn() : dto.getType().name());
        entity.setNameBn(dto.getName() != null ? dto.getName().getBn() : dto.getType().name());
        entity.setDescriptionEn(dto.getDescription() != null ? dto.getDescription().getEn() : "");
        entity.setDescriptionBn(dto.getDescription() != null ? dto.getDescription().getBn() : "");
        entity.setPrice(dto.getPrice() != null ? java.math.BigDecimal.valueOf(dto.getPrice()) : java.math.BigDecimal.ZERO);
        entity.setEtaLabelEn(dto.getEtaLabel() != null ? dto.getEtaLabel().getEn() : "");
        entity.setEtaLabelBn(dto.getEtaLabel() != null ? dto.getEtaLabel().getBn() : "");
        entity.setIcon(dto.getIcon() != null ? dto.getIcon() : "send");
        entity.setActive(true);

        return mapper.toDeliveryOptionDto(deliveryOptionRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteDeliveryOption(String type) {
        try {
            com.company.dakpion.dakpion.constant.DeliveryType dt = com.company.dakpion.dakpion.constant.DeliveryType.valueOf(type);
            deliveryOptionRepo.deleteById(dt);
        } catch (Exception e) {
            log.warn("Invalid delivery type: {}", type);
        }
    }

    @Override
    @Transactional
    public PricingPlanDto savePricingPlan(PricingPlanDto dto) {
        String id = dto.getId() != null && !dto.getId().isBlank()
                ? dto.getId()
                : "plan_" + System.currentTimeMillis();

        com.company.dakpion.dakpion.entity.DakpionPricingPlanEntity entity = pricingPlanRepo.findById(id)
                .orElse(new com.company.dakpion.dakpion.entity.DakpionPricingPlanEntity());

        entity.setId(id);
        entity.setNameEn(dto.getName() != null ? dto.getName().getEn() : id);
        entity.setNameBn(dto.getName() != null ? dto.getName().getBn() : id);
        entity.setTaglineEn(dto.getTagline() != null ? dto.getTagline().getEn() : "");
        entity.setTaglineBn(dto.getTagline() != null ? dto.getTagline().getBn() : "");
        entity.setBillingUnitEn(dto.getBillingUnit() != null ? dto.getBillingUnit().getEn() : "per letter");
        entity.setBillingUnitBn(dto.getBillingUnit() != null ? dto.getBillingUnit().getBn() : "চিঠি প্রতি");
        entity.setPrice(dto.getPrice() != null ? java.math.BigDecimal.valueOf(dto.getPrice()) : java.math.BigDecimal.ZERO);
        entity.setHighlighted(dto.getHighlighted() != null ? dto.getHighlighted() : false);
        if (dto.getFeatures() != null) {
            try {
                entity.setFeatures(objectMapper.writeValueAsString(dto.getFeatures()));
            } catch (Exception e) {
                log.warn("Failed to serialize pricing features", e);
            }
        }
        entity.setActive(true);

        return mapper.toPricingPlanDto(pricingPlanRepo.save(entity));
    }

    @Override
    @Transactional
    public void deletePricingPlan(String id) {
        pricingPlanRepo.deleteById(id);
    }

    @Override
    @Transactional
    public FontDto saveFont(FontDto dto) {
        String id = dto.getId() != null && !dto.getId().isBlank()
                ? dto.getId()
                : "font_" + System.currentTimeMillis();

        com.company.dakpion.dakpion.entity.DakpionFontEntity entity = fontRepo.findById(id)
                .orElse(new com.company.dakpion.dakpion.entity.DakpionFontEntity());

        entity.setId(id);
        entity.setName(dto.getName() != null ? dto.getName() : id);
        entity.setFontFamily(dto.getFontFamily() != null ? dto.getFontFamily() : "serif");
        entity.setCategory(dto.getCategory() != null ? dto.getCategory() : "bengali");
        entity.setCssUrl(dto.getCssUrl());
        entity.setPreviewSample(dto.getPreviewSample() != null ? dto.getPreviewSample() : "আমার সোনার বাংলা");
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        return mapper.toFontDto(fontRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteFont(String id) {
        fontRepo.deleteById(id);
    }

    @Override
    @Transactional
    public FaqItemDto saveFaq(FaqItemDto dto) {
        String id = dto.getId() != null && !dto.getId().isBlank()
                ? dto.getId()
                : "faq_" + System.currentTimeMillis();

        com.company.dakpion.dakpion.entity.DakpionFaqEntity entity = faqRepo.findById(id)
                .orElse(new com.company.dakpion.dakpion.entity.DakpionFaqEntity());

        entity.setId(id);
        entity.setQuestionEn(dto.getQuestion() != null ? dto.getQuestion().getEn() : "");
        entity.setQuestionBn(dto.getQuestion() != null ? dto.getQuestion().getBn() : "");
        entity.setAnswerEn(dto.getAnswer() != null ? dto.getAnswer().getEn() : "");
        entity.setAnswerBn(dto.getAnswer() != null ? dto.getAnswer().getBn() : "");
        entity.setActive(true);

        return mapper.toFaqItemDto(faqRepo.save(entity));
    }

    @Override
    @Transactional
    public void deleteFaq(String id) {
        faqRepo.deleteById(id);
    }
}
