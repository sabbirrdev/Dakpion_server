package com.company.efood.sys.services;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.CategoryDto;

import java.util.List;

public interface CategoryService extends BaseService<CategoryDto> {

    List<BaseDropdownModel> getDropdownListByParentId(Long parentId, Long userId);
}
