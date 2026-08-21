package com.company.efood.sys.services;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.ExclusiveOfferDto;
import com.company.efood.sys.entity.ExclusiveOffer;
import com.company.efood.sys.repository.ExclusiveOfferRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExclusiveOfferService {

    private final ExclusiveOfferRepo exclusiveOfferRepo;
    private final ModelMapper modelMapper;
    private final BaseUtils baseUtils;

    public List<ExclusiveOfferDto> getActiveOffers() {
        List<ExclusiveOffer> offers = exclusiveOfferRepo.findByIsActiveTrue();
        if (offers.isEmpty()) {
            // Seed defaults if empty
            seedDefaultOffers();
            offers = exclusiveOfferRepo.findByIsActiveTrue();
        }
        return offers.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<ExclusiveOfferDto> getPageableOffers(BasePageableRequest request) {
        PageRequest pageRequest = baseUtils.getPageRequest(request.getPage(), request.getSize());
        Page<ExclusiveOffer> page = exclusiveOfferRepo.findAllByOrderByIdDesc(pageRequest);
        return new PageImpl<>(page.stream().map(this::toDto).collect(Collectors.toList()), pageRequest, page.getTotalElements());
    }

    public ExclusiveOfferDto save(ExclusiveOfferDto dto, Long userId) {
        ExclusiveOffer entity = new ExclusiveOffer();
        if (dto.getId() != null) {
            entity = exclusiveOfferRepo.findById(dto.getId()).orElse(new ExclusiveOffer());
        }
        entity.setCode(dto.getCode().trim().toUpperCase());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setTag(dto.getTag());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setDiscountType(dto.getDiscountType());
        entity.setMinOrderAmount(dto.getMinOrderAmount());
        entity.setBannerUrl(dto.getBannerUrl());
        entity.setColorHex(dto.getColorHex() != null ? dto.getColorHex() : "#7C3AED");
        entity.setValidTill(dto.getValidTill());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setActive(true);
        if (entity.getId() == null) {
            entity.setEntryDate(LocalDateTime.now());
            entity.setEntryUser(userId != null ? userId : 0L);
        } else {
            entity.setUpdateDate(LocalDateTime.now());
            entity.setUpdateUser(userId != null ? userId : 0L);
        }
        ExclusiveOffer saved = exclusiveOfferRepo.save(entity);
        return toDto(saved);
    }

    public boolean delete(Long id) {
        if (id != null && exclusiveOfferRepo.existsById(id)) {
            exclusiveOfferRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private void seedDefaultOffers() {
        List<ExclusiveOffer> defaults = List.of(
                createSeedOffer("EFOOD50", "50% OFF First Food Order", "Valid on food orders above ৳300. Max discount ৳150.", "HOT FOOD DEAL", "#7C3AED", "Valid till 31 Aug 2026"),
                createSeedOffer("PHARMA24", "Free Medicine Delivery", "Free express delivery on pharma orders above ৳500.", "PHARMA SPECIAL", "#0D9488", "Always Active"),
                createSeedOffer("RIDE100", "৳100 Off Bike / Car Ride", "Flat ৳100 discount on your next 3 ride share trips.", "RIDE SHARE", "#2563EB", "Valid for 7 days"),
                createSeedOffer("FRESHGROCERY", "৳80 Off Fresh Groceries", "Discount on daily fresh vegetables & supermarket items above ৳600.", "GROCERY DEAL", "#16A34A", "Valid till 15 Sep 2026")
        );
        exclusiveOfferRepo.saveAll(defaults);
    }

    private ExclusiveOffer createSeedOffer(String code, String title, String desc, String tag, String color, String validTill) {
        ExclusiveOffer o = new ExclusiveOffer();
        o.setCode(code);
        o.setTitle(title);
        o.setDescription(desc);
        o.setTag(tag);
        o.setColorHex(color);
        o.setValidTill(validTill);
        o.setIsActive(true);
        o.setActive(true);
        o.setEntryDate(LocalDateTime.now());
        o.setEntryUser(0L);
        return o;
    }

    public ExclusiveOfferDto toDto(ExclusiveOffer entity) {
        ExclusiveOfferDto dto = new ExclusiveOfferDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setTag(entity.getTag());
        dto.setDiscountValue(entity.getDiscountValue());
        dto.setDiscountType(entity.getDiscountType());
        dto.setMinOrderAmount(entity.getMinOrderAmount());
        dto.setBannerUrl(entity.getBannerUrl());
        dto.setColorHex(entity.getColorHex());
        dto.setValidTill(entity.getValidTill());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setIsActive(entity.getIsActive());
        dto.setActive(entity.getActive());
        return dto;
    }
}
