package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.RefreshTokenDto;
import com.company.efood.sys.entity.RefreshToken;
import com.company.efood.sys.repository.RefreshTokenRepo;
import com.company.efood.sys.services.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final BaseUtils baseUtils;
    private RefreshTokenRepo refreshTokenRepo;
    private ModelMapper modelMapper;


    @Override
    public RefreshTokenDto save(RefreshTokenDto obj, Long userId) {
        RefreshToken savedEntity = refreshTokenRepo.save(generateEntity(obj, userId, true));
        return generateDto(savedEntity);
    }

    @Override
    public RefreshTokenDto update(RefreshTokenDto obj, Long userId) {
        RefreshToken savedEntity = refreshTokenRepo.save(generateEntity(obj, userId, false));
        return generateDto(savedEntity);
    }

    @Override
    public boolean delete(RefreshTokenDto obj, Long userId) {
        return false;
    }

    @Override
    public RefreshTokenDto getById(Long id, Long userId) {
        return null;
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }


    @Override
    public Page<RefreshTokenDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        return null;
    }

    @Override
    public boolean isRefreshTokenPresent(Long userId) {
        return refreshTokenRepo.findByAppUserId(userId.intValue()).isPresent();
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return refreshTokenRepo.findByRefreshToken(token).map(this::generateDto);
    }

    private RefreshToken generateEntity(RefreshTokenDto dto, Long userId, Boolean isSaved) {
        RefreshToken entity = new RefreshToken();
        if (isSaved) {
            BeanUtils.copyProperties(dto, entity);
            if (dto.getAppUserId() != null) {
                entity.setAppUserId(dto.getAppUserId().intValue());
            }
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            RefreshToken dbEntity = refreshTokenRepo.findByAppUserId(dto.getAppUserId().intValue()).get();
            System.out.println("database entity" + dbEntity);
            BeanUtils.copyProperties(dbEntity, entity);
            if (dto.getRefreshToken() != null) {
                entity.setRefreshToken(dto.getRefreshToken());
            }
            if (dto.getAppUserId() != null) {
                entity.setAppUserId(dto.getAppUserId().intValue());
            }
            entity.setExpireTime(Instant.now().plusMillis(60 * 60 * 1000));
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private List<RefreshTokenDto> convertEntityListToDtoList(Stream<RefreshToken> entityList) {
        return entityList.map(entity -> {
            return generateDto(entity);
        }).collect(Collectors.toList());
    }


    public RefreshTokenDto generateDto(RefreshToken entity) {
        RefreshTokenDto dto = modelMapper.map(entity, RefreshTokenDto.class);
        if (entity != null && entity.getAppUserId() != null) {
            dto.setAppUserId(entity.getAppUserId().longValue());
        }
        return dto;
    }


}
