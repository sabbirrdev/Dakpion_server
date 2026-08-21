package com.company.efood.sys.services;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.MenuItemDto;
import com.company.efood.sys.entity.MenuItem;

import java.util.List;

public interface MenuItemService extends BaseService<MenuItemDto> {
    List<MenuItem> getByAppUserId(Long appUserId);

    List<BaseDropdownModel> getDropdownListByMenuType(String menuTypeString, Long userId);

    List<BaseDropdownModel> getModuleList(Long userId);

//     List<MenuItemDto> getAuthorizedReportList(Integer userId, Integer moduleId);
//
//    List<MenuItem> getPageByAppUserId(Integer appUserId, int userId);


}
