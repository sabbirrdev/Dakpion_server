package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.UserRoleAssignDetailsDto;
import com.company.efood.sys.dto.UserRoleAssignMasterDto;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.entity.UserRoleAssignDetails;
import com.company.efood.sys.entity.UserRoleAssignMaster;
import com.company.efood.sys.entity.UserRoleMaster;
import com.company.efood.sys.model.UserRoleAssignModel;
import com.company.efood.sys.repository.UserRoleAssignDetailsRepo;
import com.company.efood.sys.repository.UserRoleAssignMasterRepo;
import com.company.efood.sys.services.UserRoleAssignService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class UserRoleAssignServiceImpl implements UserRoleAssignService {

    private final BaseUtils baseUtils;
    private ModelMapper modelMapper;
    private UserRoleAssignMasterRepo userRoleAssignMasterRepo;
    private UserRoleAssignDetailsRepo userRoleAssignDetailsRepo;

    @Transactional
    @Override
    public UserRoleAssignModel save(UserRoleAssignModel obj, Long userId) {

        UserRoleAssignMaster entity = userRoleAssignMasterRepo.save(generateMasterEntity(obj.getMaster(), userId, true));
        /*save details*/
        return saveUserRoleAssignModel(obj, userId, entity);
    }

    @Override
    @Transactional
    public UserRoleAssignModel update(UserRoleAssignModel obj, Long userId) {
        UserRoleAssignMaster entity = userRoleAssignMasterRepo.save(generateMasterEntity(obj.getMaster(), userId, false));
        /*save details*/
        return saveUserRoleAssignModel(obj, userId, entity);
    }


    @Override
    @Transactional
    public boolean delete(UserRoleAssignModel obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getMaster().getId())) {
            UserRoleAssignMaster entity = new UserRoleAssignMaster();
            entity.setId(obj.getMaster().getId());
            userRoleAssignMasterRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserRoleAssignModel getById(Long id, Long userId) {
        return null;
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }


    @Override
    public Page<UserRoleAssignModel> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(),pageableBodyRequest.getSize());
        Page<UserRoleAssignMaster> userRoleAssignMasterPage = userRoleAssignMasterRepo.findAll(pageRequest);
        List<UserRoleAssignModel> userRoleAssignModelList = convertMasterToDetails(userRoleAssignMasterPage.getContent());
        return new PageImpl<>(userRoleAssignModelList,pageRequest,userRoleAssignMasterPage.getTotalElements());
    }


    private UserRoleAssignModel saveUserRoleAssignModel(UserRoleAssignModel obj, Long userId, UserRoleAssignMaster entity) {
        if (!ObjectUtils.isEmpty(obj.getMaster())) {
            List<UserRoleAssignDetails> listForSave = new ArrayList<>();
            for (UserRoleAssignDetailsDto detail : obj.getDetailsList()) {
                listForSave.add(generateDetailsEntity(detail, entity, userId));
            }
            userRoleAssignDetailsRepo.saveAll(listForSave);
        }

        return obj;
    }


    /* master part */
    private UserRoleAssignMaster generateMasterEntity(UserRoleAssignMasterDto dto, Long userId, Boolean isSaved) {
        UserRoleAssignMaster entity = new UserRoleAssignMaster();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            UserRoleAssignMaster dbEntity = userRoleAssignMasterRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        if (!ObjectUtils.isEmpty(dto.getAppUserId())) {
            AppUser appUser = new AppUser();
            appUser.setId(dto.getAppUserId());
            entity.setAppUser(appUser);
        }
        return entity;
    }


    private UserRoleAssignMasterDto generateMasterDto(UserRoleAssignMaster entity) {
        UserRoleAssignMasterDto dto = modelMapper.map(entity, UserRoleAssignMasterDto.class);
        if (!ObjectUtils.isEmpty(entity.getAppUser())) {
            dto.setAppUserId(entity.getAppUser().getId());
            dto.setAppUserName(entity.getAppUser().getUsername() + " - " + entity.getAppUser().getDisplayName());
        }
        return dto;
    }


    /* details part */
    private UserRoleAssignDetails generateDetailsEntity(UserRoleAssignDetailsDto dto, UserRoleAssignMaster masterEntity, Long userId) {
        UserRoleAssignDetails entity = new UserRoleAssignDetails();
        BeanUtils.copyProperties(dto, entity);
        entity.setMaster(masterEntity);
        entity.setEntryUser(userId);
        baseUtils.setEntryUserInfo(entity);
        if (!ObjectUtils.isEmpty(dto.getUserRoleId())) {
            UserRoleMaster obj = new UserRoleMaster();
            obj.setId(dto.getUserRoleId());
            entity.setUserRole(obj);
        }
        return entity;
    }

    private UserRoleAssignDetailsDto generateDetailsDto(UserRoleAssignDetails entity) {
        UserRoleAssignDetailsDto dto = modelMapper.map(entity, UserRoleAssignDetailsDto.class);
        if (!ObjectUtils.isEmpty(entity.getUserRole())) {
            dto.setUserRoleId(entity.getUserRole().getId());
            dto.setUserRoleName(entity.getUserRole().getName());
        }
        return dto;
    }

    private List<UserRoleAssignDetailsDto> convertDetailsEntityListToDtoList(Stream<UserRoleAssignDetails> entityList) {
        return entityList.map(entity -> {
            return generateDetailsDto(entity);
        }).collect(Collectors.toList());
    }


    //........................ Helper ................................//

    private void deleteDetilsData(UserRoleAssignMaster savedEntity, List<UserRoleAssignDetailsDto> details) {
        List<UserRoleAssignDetails> listForCheckDelete = userRoleAssignDetailsRepo.findByMasterId(savedEntity.getId()); // 1,2,3
        List<UserRoleAssignDetails> listForDelete = new ArrayList<>();
        for (UserRoleAssignDetails obj : listForCheckDelete) {
            boolean needToDelete = true;
            for (UserRoleAssignDetailsDto detailsEntity : details) {
                if (obj.getId().equals(detailsEntity.getId())) {
                    needToDelete = false;
                    break;
                }
            }
            if (needToDelete) {
                listForDelete.add(obj);
            }
        }
        userRoleAssignDetailsRepo.deleteAll(listForDelete);
    }

    private List<UserRoleAssignModel> convertMasterToDetails(List<UserRoleAssignMaster> list) {
        List<UserRoleAssignModel> returnList = new ArrayList<>();
        for (UserRoleAssignMaster master : list) {
            UserRoleAssignModel tmp = new UserRoleAssignModel();
            /*set master*/
            tmp.setMaster(generateMasterDto(master));
            /*set details*/
            tmp.setDetailsList(convertDetailsEntityListToDtoList(userRoleAssignDetailsRepo.findByMasterId(master.getId()).stream()));
            returnList.add(tmp);
        }
        return returnList;
    }


}
