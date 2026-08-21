package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.CommissionPolicyDto;
import com.company.efood.sys.entity.CommissionPolicy;
import com.company.efood.sys.repository.CommissionPolicyRepo;
import com.company.efood.sys.services.CommissionPolicyService;
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
public class CommissionPolicyServiceImpl implements CommissionPolicyService {

    private final BaseUtils baseUtils;
    private final CommissionPolicyRepo commissionPolicyRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CommissionPolicyDto save(CommissionPolicyDto obj, Long userId) {
        CommissionPolicy savedEntity = commissionPolicyRepo.save(generateEntity(obj, userId, true));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public CommissionPolicyDto update(CommissionPolicyDto obj, Long userId) {
        CommissionPolicy savedEntity = commissionPolicyRepo.save(generateEntity(obj, userId, false));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public boolean delete(CommissionPolicyDto obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getId())) {
            CommissionPolicy entity = new CommissionPolicy();
            entity.setId(obj.getId());
            commissionPolicyRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public CommissionPolicyDto getById(Long id, Long userId) {
        Optional<CommissionPolicy> data = commissionPolicyRepo.findById(id);
        return data.map(this::generateDto).orElse(null);
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }

    @Override
    public Page<CommissionPolicyDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(), pageableBodyRequest.getSize());
        Page<CommissionPolicy> page = commissionPolicyRepo.findAll(pageRequest);
        List<CommissionPolicyDto> objList = convertEntityListToDtoList(page.stream());
        return new PageImpl<>(objList, pageRequest, page.getTotalElements());
    }

    //..................... Generate Model....................//

    private CommissionPolicy generateEntity(CommissionPolicyDto dto, Long userId, Boolean isSaved) {
        CommissionPolicy entity = new CommissionPolicy();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            CommissionPolicy dbEntity = commissionPolicyRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private List<CommissionPolicyDto> convertEntityListToDtoList(Stream<CommissionPolicy> entityList) {
        return entityList.map(this::generateDto).collect(Collectors.toList());
    }

    public CommissionPolicyDto generateDto(CommissionPolicy entity) {
        return modelMapper.map(entity, CommissionPolicyDto.class);
    }
}
