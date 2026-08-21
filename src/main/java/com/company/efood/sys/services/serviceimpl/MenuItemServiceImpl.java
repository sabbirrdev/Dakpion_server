package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.MenuItemDto;
import com.company.efood.sys.entity.MenuItem;
import com.company.efood.sys.repository.MenuItemRepo;
import com.company.efood.sys.services.MenuItemService;
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


@Service
@AllArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private ModelMapper modelMapper;
    private MenuItemRepo menuItemRepo;
    private BaseUtils baseUtils;

    @Transactional
    @Override
    public MenuItemDto save(MenuItemDto menuItemDto, Long userId) {
        MenuItem savedEntity = menuItemRepo.save(generateEntity(menuItemDto, userId, true));
        return generateDto(savedEntity);
    }

    @Transactional
    @Override
    public MenuItemDto update(MenuItemDto menuItemDto, Long userId) {
        MenuItem savedEntity = menuItemRepo.save(generateEntity(menuItemDto, userId, false));
        return generateDto(savedEntity);
    }

    @Transactional
    @Override
    public boolean delete(MenuItemDto obj, Long userId) {
        if (!ObjectUtils.isEmpty(obj.getId())) {
            MenuItem entity = new MenuItem();
            entity.setId(obj.getId());
            menuItemRepo.delete(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public MenuItemDto getById(Long id, Long userId) {
        Optional<MenuItem> dataList = menuItemRepo.findById(id);
        if (dataList.isEmpty()) {
            return null;
        } else {
            return generateDto(dataList.get());
        }
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {

        return menuItemRepo.findDropdownModel();
    }


    @Override
    public Page<MenuItemDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(),pageableBodyRequest.getSize());
        Page<MenuItem> menuItemPage = menuItemRepo.findAll(pageRequest);
        List<MenuItemDto> objList = convertEntityListToDtoList(menuItemPage.stream());
        return new  PageImpl<>(objList,pageRequest,menuItemPage.getTotalElements());
    }


    @Override
    @Transactional
    public List<MenuItem> getByAppUserId(Long appUserId) {
        return menuItemRepo.findMenuItemByUserId(appUserId);
    }

    @Override
    public List<BaseDropdownModel> getDropdownListByMenuType(String menuTypeString, Long userId) {
        String [] menuTypes = menuTypeString.split(",");
        List<Integer> menuTypeList = new ArrayList<>();
        for(String menuType: menuTypes){
            menuTypeList.add(Integer.parseInt(menuType));
        }
        return menuItemRepo.findDropdownModelByMenuType(menuTypeList);
    }

    @Override
    public List<BaseDropdownModel> getModuleList(Long userId) {
        return menuItemRepo.findModuleDropdownModel();
    }

    //==============================================================
    //              Convert Dto to Entity && Entity to Dto
    //==============================================================
    private MenuItem generateEntity(MenuItemDto dto, Long userId, Boolean isSaved) {
        MenuItem entity = new MenuItem();
        BeanUtils.copyProperties(dto, entity);
        if (isSaved) {
            entity.setEntryUser(userId);
            baseUtils.setEntryUserInfo(entity);
        } else {
            MenuItem dbEntity = menuItemRepo.findById(dto.getId()).get();
            entity.setUpdateUser(userId);
            baseUtils.setUpdateUserInfo(entity, dbEntity);
        }
        return entity;
    }

    private MenuItemDto generateDto(MenuItem entity) {
        MenuItemDto dto = modelMapper.map(entity, MenuItemDto.class);
        if(!ObjectUtils.isEmpty(entity.getParent())) {
            dto.setParentId(entity.getParent().getId());
            dto.setParentName(entity.getParent().getName());

        }
        return dto;
    }


    private List<MenuItemDto> convertEntityListToDtoList(Stream<MenuItem> entityList) {
        return entityList.map(entity -> {
            return generateDto(entity);
        }).collect(Collectors.toList());
    }


}
