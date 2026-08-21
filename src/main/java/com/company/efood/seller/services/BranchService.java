package com.company.efood.seller.services;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseService;
import com.company.efood.seller.dto.BranchDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BranchService extends BaseService<BranchDto> {

    Page<BranchDto> getPageableAllByUserLocation(BasePageableRequest pageableBodyRequest);
    List<BaseDropdownModel> getDropdownListByShopId(Long shopId,Long userId);
}
