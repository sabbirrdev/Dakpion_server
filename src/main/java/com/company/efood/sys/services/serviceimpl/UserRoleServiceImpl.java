package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.UserRoleDetailsDto;
import com.company.efood.sys.dto.UserRoleMasterDto;
import com.company.efood.sys.entity.MenuItem;
import com.company.efood.sys.entity.UserRoleDetails;
import com.company.efood.sys.entity.UserRoleMaster;
import com.company.efood.sys.model.UserRoleModel;
import com.company.efood.sys.repository.UserRoleDetailsRepo;
import com.company.efood.sys.repository.UserRoleMasterRepo;
import com.company.efood.sys.services.UserRoleService;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final BaseUtils baseUtils;
    private ModelMapper modelMapper;
    private UserRoleMasterRepo userRoleMasterRepo;
    private UserRoleDetailsRepo userRoleDetailsRepo;


    @Transactional
    @Override
    public UserRoleModel save(UserRoleModel obj, Long userId) {

        UserRoleMaster userRoleMaster = userRoleMasterRepo.save(generateMasterEntity(obj.getUserRoleMaster(), userId, true));

        if (!ObjectUtils.isEmpty(obj.getUserRoleMaster())) {
            List<UserRoleDetails> listForSave = new ArrayList<>();
            for (UserRoleDetailsDto userRoleDetailsDto : obj.getUserRoleDetailsList()) {
                listForSave.add(generateDetailsEntity(userRoleDetailsDto, userRoleMaster, userId));
            }
            userRoleDetailsRepo.saveAll(listForSave);
        }
        obj.setUserRoleMaster(generateMasterDto(userRoleMaster));
        return obj;
    }

    @Transactional
    @Override
    public UserRoleModel update(UserRoleModel obj, Long userId) {
        UserRoleMaster userRoleMaster = userRoleMasterRepo.save(generateMasterEntity(obj.getUserRoleMaster(), userId, false));
        if (!ObjectUtils.isEmpty(obj.getUserRoleMaster())) {
            /* delete previous data if needed */
            deleteDetailsData(userRoleMaster, obj.getUserRoleDetailsList());
            List<UserRoleDetails> listForSave = new ArrayList<>();
            for (UserRoleDetailsDto userRoleDetailsDto : obj.getUserRoleDetailsList()) {
                listForSave.add(generateDetailsEntity(userRoleDetailsDto, userRoleMaster, userId));
            }
            userRoleDetailsRepo.saveAll(listForSave);

        }
        obj.setUserRoleMaster(generateMasterDto(userRoleMaster));
        return obj;
    }

    @Transactional
    @Override
    public boolean delete(UserRoleModel obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getUserRoleMaster().getId())) {
            UserRoleMaster entity = new UserRoleMaster();
            entity.setId(obj.getUserRoleMaster().getId());
            userRoleMasterRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UserRoleModel getById(Long id, Long userId) {
        Optional<UserRoleMaster> dataList = userRoleMasterRepo.findById(id);
        if(dataList.isEmpty()) {
            return null;
        }else {
            List<UserRoleMaster> masterList = new ArrayList<>();
            masterList.add(dataList.get());
            return convertMasterToDetails(masterList).get(0);
        }
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return null;
    }

    @Override
    public Page<UserRoleModel> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(),pageableBodyRequest.getSize());
        Page<UserRoleMaster> userRoleModelPage = userRoleMasterRepo.findAll(pageRequest);
        List<UserRoleModel> userRoleModelList = convertMasterToDetails(userRoleModelPage.getContent());
        return new PageImpl<>(userRoleModelList,pageRequest,userRoleModelPage.getTotalElements());
    }

    @Override
    public List<UserRoleMasterDto> getRoleListByUser(Long appUserId, Long userId) {
        return null;
    }


    /* master part */
    private UserRoleMaster generateMasterEntity(UserRoleMasterDto dto, Long userId, Boolean isSaved) {
        UserRoleMaster entity = new UserRoleMaster();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            UserRoleMaster dbEntity = userRoleMasterRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private UserRoleMasterDto generateMasterDto(UserRoleMaster entity) {
        UserRoleMasterDto dto = modelMapper.map(entity, UserRoleMasterDto.class);
        return dto;
    }

    /* details part */
    private UserRoleDetails generateDetailsEntity(UserRoleDetailsDto dto, UserRoleMaster masterEntity, Long userId) {
        UserRoleDetails entity = new UserRoleDetails();
        BeanUtils.copyProperties(dto, entity);
        entity.setMaster(masterEntity);
        entity.setEntryUser(userId);
        baseUtils.setEntryUserInfo(entity);

        if (!ObjectUtils.isEmpty(dto.getMenuItemId())) {
            MenuItem menuItem = new MenuItem();
            menuItem.setId(dto.getMenuItemId());
            entity.setMenuItem(menuItem);
        } else {
            entity.setMenuItem(null);
        }
        return entity;
    }

    private UserRoleDetailsDto generateDetailsDto(UserRoleDetails entity) {
        UserRoleDetailsDto dto = modelMapper.map(entity, UserRoleDetailsDto.class);
        if (!ObjectUtils.isEmpty(entity.getMenuItem())) {
            dto.setMenuItemId(entity.getMenuItem().getId());
            dto.setMenuItemName(entity.getMenuItem().getName() + " (" + entity.getMenuItem().getMenuTypeName() + ")");
        }
        return dto;
    }

    private List<UserRoleDetailsDto> convertDetailsEntityListToDtoList(Stream<UserRoleDetails> entityList) {
        return entityList.map(entity -> {
            return generateDetailsDto(entity);
        }).collect(Collectors.toList());
    }

    private void deleteDetailsData(UserRoleMaster savedEntity, List<UserRoleDetailsDto> details) {
        List<UserRoleDetails> listForCheckDelete = userRoleDetailsRepo.findByMasterId(savedEntity.getId()); // 1,2,3
        List<UserRoleDetails> listForDelete = new ArrayList<>();
        for (UserRoleDetails obj : listForCheckDelete) {
            boolean needToDelete = true;
            for (UserRoleDetailsDto detailsEntity : details) { // 1,3
                if (obj.getId().equals(detailsEntity.getId())) {
                    needToDelete = false;
                    break;
                }
            }
            if (needToDelete) {
                listForDelete.add(obj);
            }
        }
        userRoleDetailsRepo.deleteAll(listForDelete);
    }

    //Helper Functions.............................

    private List<UserRoleModel> convertMasterToDetails(List<UserRoleMaster> list) {
        List<UserRoleModel> returnList = new ArrayList<>();
        for (UserRoleMaster master : list) {

            UserRoleModel tmp = new UserRoleModel();
            /*set master*/
            tmp.setUserRoleMaster(generateMasterDto(master));
            /*set details*/
            tmp.setUserRoleDetailsList(convertDetailsEntityListToDtoList(userRoleDetailsRepo.findByMasterId(master.getId()).stream()));
            returnList.add(tmp);
        }
        return returnList;
    }


}
