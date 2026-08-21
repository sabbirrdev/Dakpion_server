package com.company.efood.catalog.service;

import com.company.efood.catalog.dto.ShopInventoryItemDto;
import com.company.efood.base.BasePageableRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShopInventoryItemService {
    ShopInventoryItemDto save(ShopInventoryItemDto dto, Long userId);
    ShopInventoryItemDto update(ShopInventoryItemDto dto, Long userId);
    boolean delete(ShopInventoryItemDto dto, Long userId);
    ShopInventoryItemDto getById(Long id, Long userId);
    List<ShopInventoryItemDto> getAll(Long userId);
    Page<ShopInventoryItemDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId);
}
