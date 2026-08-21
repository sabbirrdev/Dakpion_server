package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.DeliveryFeePolicyDto;
import com.company.efood.sys.entity.DeliveryFeePolicy;
import com.company.efood.sys.repository.DeliveryFeePolicyRepo;
import com.company.efood.sys.services.DeliveryFeePolicyService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class DeliveryFeePolicyServiceImpl implements DeliveryFeePolicyService {

    private final BaseUtils baseUtils;
    private final DeliveryFeePolicyRepo deliveryFeePolicyRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public DeliveryFeePolicyDto save(DeliveryFeePolicyDto obj, Long userId) {
        DeliveryFeePolicy savedEntity = deliveryFeePolicyRepo.save(generateEntity(obj, userId, true));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public DeliveryFeePolicyDto update(DeliveryFeePolicyDto obj, Long userId) {
        DeliveryFeePolicy savedEntity = deliveryFeePolicyRepo.save(generateEntity(obj, userId, false));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public boolean delete(DeliveryFeePolicyDto obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getId())) {
            DeliveryFeePolicy entity = new DeliveryFeePolicy();
            entity.setId(obj.getId());
            deliveryFeePolicyRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public DeliveryFeePolicyDto getById(Long id, Long userId) {
        Optional<DeliveryFeePolicy> data = deliveryFeePolicyRepo.findById(id);
        return data.map(this::generateDto).orElse(null);
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }

    @Override
    public Page<DeliveryFeePolicyDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(), pageableBodyRequest.getSize());
        Page<DeliveryFeePolicy> page = deliveryFeePolicyRepo.findAll(pageRequest);
        List<DeliveryFeePolicyDto> objList = convertEntityListToDtoList(page.stream());
        return new PageImpl<>(objList, pageRequest, page.getTotalElements());
    }

    //..................... Generate Model....................//

    private DeliveryFeePolicy generateEntity(DeliveryFeePolicyDto dto, Long userId, Boolean isSaved) {
        DeliveryFeePolicy entity = new DeliveryFeePolicy();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            DeliveryFeePolicy dbEntity = deliveryFeePolicyRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private List<DeliveryFeePolicyDto> convertEntityListToDtoList(Stream<DeliveryFeePolicy> entityList) {
        return entityList.map(this::generateDto).collect(Collectors.toList());
    }

    public DeliveryFeePolicyDto generateDto(DeliveryFeePolicy entity) {
        return modelMapper.map(entity, DeliveryFeePolicyDto.class);
    }
}
