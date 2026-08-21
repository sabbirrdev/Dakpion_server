package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.MenuItemDto;
import com.company.efood.sys.dto.PasswordPolicyDto;
import com.company.efood.sys.entity.MenuItem;
import com.company.efood.sys.entity.PasswordPolicy;
import com.company.efood.sys.repository.PasswordPolicyRepo;
import com.company.efood.sys.services.PasswordPolicyService;
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
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    private final BaseUtils baseUtils;
    private final PasswordPolicyRepo passwordPolicyRepo;
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PasswordPolicyDto save(PasswordPolicyDto obj, Long userId) {
        PasswordPolicy savedEntity = passwordPolicyRepo.save(generateEntity(obj, userId, true));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public PasswordPolicyDto update(PasswordPolicyDto obj, Long userId) {
        PasswordPolicy savedEntity = passwordPolicyRepo.save(generateEntity(obj, userId, false));
        return generateDto(savedEntity);
    }

    @Override
    @Transactional
    public boolean delete(PasswordPolicyDto obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getId())) {
            PasswordPolicy entity = new PasswordPolicy();
            entity.setId(obj.getId());
            passwordPolicyRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public PasswordPolicyDto getById(Long id, Long userId) {
        Optional<PasswordPolicy> dataList = passwordPolicyRepo.findById(id);
        if (dataList.isEmpty()) {
            return null;
        } else {
            return generateDto(dataList.get());
        }
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }


    @Override
    public Page<PasswordPolicyDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(),pageableBodyRequest.getSize());
        Page<PasswordPolicy> menuItemPage = passwordPolicyRepo.findAll(pageRequest);
        List<PasswordPolicyDto> objList = convertEntityListToDtoList(menuItemPage.stream());
        return new PageImpl<>(objList,pageRequest,menuItemPage.getTotalElements());
    }


    @Override
    @Transactional
    public PasswordPolicyDto getPublicPasswordPolicyById(Long id) {
        PasswordPolicy dataList = passwordPolicyRepo.findById(id).get();
        return generateDto(dataList);
    }

    //..................... Generate Model....................//

    private PasswordPolicy generateEntity(PasswordPolicyDto dto, Long userId, Boolean isSaved) {

        PasswordPolicy entity = new PasswordPolicy();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            PasswordPolicy dbEntity = passwordPolicyRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private List<PasswordPolicyDto> convertEntityListToDtoList(Stream<PasswordPolicy> entityList) {
        return entityList.map(entity -> {
            return generateDto(entity);
        }).collect(Collectors.toList());
    }


    public PasswordPolicyDto generateDto(PasswordPolicy entity) {
        PasswordPolicyDto dto = modelMapper.map(entity, PasswordPolicyDto.class);
        return dto;
    }

}
