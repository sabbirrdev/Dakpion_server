package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.AddressDto;
import com.company.efood.sys.entity.Address;
import com.company.efood.sys.repository.AddressRepo;
import com.company.efood.sys.utils.AddressType;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.zone.entity.Upazila;
import com.company.efood.zone.entity.Zone;
import com.company.efood.zone.repository.UpazilaRepository;
import com.company.efood.zone.repository.ZoneRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "address")
@AllArgsConstructor
public class AddressController {

    private final AddressRepo addressRepo;
    private final ZoneRepository zoneRepo;
    private final UpazilaRepository upazilaRepo;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping("/save")
    public BaseResponse saveAddress(@RequestBody AddressDto dto, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            System.out.println("📍 [ADDRESS CTRL] saveAddress called by userId=" + userId + " | type=" + dto.getAddressType() + " | label=" + dto.getLabel());

            Address entity = null;

            if (dto.getId() != null) {
                entity = addressRepo.findById(dto.getId()).orElse(null);
            }

            // If no ID, check if user already has an address with matching addressType AND matching address/label
            if (entity == null && dto.getAddressType() != null) {
                List<Address> userAddrs = addressRepo.findByEntryUserAndActiveTrue(userId);
                entity = userAddrs.stream()
                        .filter(a -> a.getAddressType() == dto.getAddressType() &&
                                (dto.getLabel() == null || dto.getLabel().equalsIgnoreCase(a.getHouseNo())))
                        .findFirst()
                        .orElse(null);
            }

            boolean isNew = (entity == null);
            if (isNew) {
                entity = new Address();
                entity.setEntryUser(userId);
                baseUtils.setEntryUserInfo(entity);
            }

            if (dto.getAddressType() != null) entity.setAddressType(dto.getAddressType());
            if (dto.getAddress() != null)     entity.setAddress(dto.getAddress());
            if (dto.getDistrict() != null)    entity.setDistrict(dto.getDistrict());
            if (dto.getPoliceStation() != null) entity.setPoliceStation(dto.getPoliceStation());
            if (dto.getPostOffice() != null)  entity.setPostOffice(dto.getPostOffice());
            if (dto.getHouseNo() != null)     entity.setHouseNo(dto.getHouseNo());
            if (dto.getRoadNo() != null)      entity.setRoadNo(dto.getRoadNo());
            if (dto.getPostCode() != null)    entity.setPostCode(dto.getPostCode());
            if (dto.getLat() != null)         entity.setLat(dto.getLat());
            if (dto.getLon() != null)         entity.setLon(dto.getLon());
            entity.setActive(true);

            // Link Zone & Upazila if provided
            if (dto.getZoneId() != null) {
                Zone zone = zoneRepo.findById(dto.getZoneId()).orElse(null);
                entity.setZone(zone);
                if (zone != null && zone.getUpazila() != null) {
                    entity.setUpazila(zone.getUpazila());
                    entity.setDistrict(zone.getUpazila().getDistrict() != null ? zone.getUpazila().getDistrict().getName() : entity.getDistrict());
                }
            }
            if (dto.getUpazilaId() != null && entity.getUpazila() == null) {
                Upazila upazila = upazilaRepo.findById(dto.getUpazilaId()).orElse(null);
                entity.setUpazila(upazila);
            }

            if (!isNew) {
                entity.setUpdateUser(userId);
                baseUtils.setUpdateUserInfo(entity, addressRepo.findById(entity.getId()).orElse(entity));
            }

            Address saved = addressRepo.save(entity);
            System.out.println("✅ [ADDRESS CTRL] Address saved with id=" + saved.getId());

            return baseUtils.generateSuccessResponse(toDto(saved), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            System.out.println("❌ [ADDRESS CTRL] Error in saveAddress: " + ex.getMessage());
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/my")
    public BaseResponse getMyAddresses(HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            List<AddressDto> dtos = addressRepo.findByEntryUserAndActiveTrue(userId)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            return baseUtils.generateSuccessResponse(dtos, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/delete/{id}")
    public BaseResponse deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            Address entity = addressRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));

            entity.setActive(false);
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, addressRepo.findById(id).orElse(entity));
            addressRepo.save(entity);

            return baseUtils.generateSuccessResponse(null, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    private AddressDto toDto(Address entity) {
        AddressDto dto = new AddressDto();
        dto.setId(entity.getId());
        dto.setAddressType(entity.getAddressType());
        dto.setAddress(entity.getAddress());
        dto.setDistrict(entity.getDistrict());
        dto.setPoliceStation(entity.getPoliceStation());
        dto.setPostOffice(entity.getPostOffice());
        dto.setHouseNo(entity.getHouseNo());
        dto.setRoadNo(entity.getRoadNo());
        dto.setPostCode(entity.getPostCode());
        dto.setLat(entity.getLat());
        dto.setLon(entity.getLon());
        dto.setActive(entity.getActive());

        if (entity.getZone() != null) {
            dto.setZoneId(entity.getZone().getId());
            dto.setZoneName(entity.getZone().getName());
            if (entity.getZone().getUpazila() != null) {
                dto.setUpazilaId(entity.getZone().getUpazila().getId());
                dto.setUpazilaName(entity.getZone().getUpazila().getName());
                if (entity.getZone().getUpazila().getDistrict() != null) {
                    dto.setDistrictId(entity.getZone().getUpazila().getDistrict().getId());
                    dto.setDistrictName(entity.getZone().getUpazila().getDistrict().getName());
                    if (entity.getZone().getUpazila().getDistrict().getDivision() != null) {
                        dto.setDivisionId(entity.getZone().getUpazila().getDistrict().getDivision().getId());
                        dto.setDivisionName(entity.getZone().getUpazila().getDistrict().getDivision().getName());
                    }
                }
            }
        }
        return dto;
    }
}
