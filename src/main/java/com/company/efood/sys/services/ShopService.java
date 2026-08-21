package com.company.efood.sys.services;

import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.ShopDto;

public interface ShopService extends BaseService<ShopDto> {
    /** Returns the shop belonging to the currently logged-in seller, or null if none. */
    ShopDto getMyShop(Long userId);
}
