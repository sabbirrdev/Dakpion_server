package com.company.efood.catalog.service.impl;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.catalog.dto.ShopInventoryItemDto;
import com.company.efood.catalog.entity.CatalogProduct;
import com.company.efood.catalog.entity.ShopInventoryItem;
import com.company.efood.catalog.repository.CatalogProductRepo;
import com.company.efood.catalog.repository.ShopInventoryItemRepo;
import com.company.efood.catalog.service.ShopInventoryItemService;
import com.company.efood.seller.repository.BranchRepo;
import com.company.efood.sys.entity.Branch;
import com.company.efood.sys.entity.Shop;
import com.company.efood.sys.repository.ShopRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShopInventoryItemServiceImpl implements ShopInventoryItemService {

    private final ShopInventoryItemRepo shopInventoryItemRepo;
    private final CatalogProductRepo catalogProductRepo;
    private final ShopRepo shopRepo;
    private final BranchRepo branchRepo;
    private final BaseUtils baseUtils;
    private final ModelMapper modelMapper;

    @Override
    public ShopInventoryItemDto save(ShopInventoryItemDto dto, Long userId) {
        ShopInventoryItem entity = modelMapper.map(dto, ShopInventoryItem.class);
        if (dto.getCatalogProductId() != null) {
            CatalogProduct catalogProduct = catalogProductRepo.findById(dto.getCatalogProductId()).orElse(null);
            entity.setCatalogProduct(catalogProduct);
        }
        if (dto.getShopId() != null) {
            Shop shop = shopRepo.findById(dto.getShopId()).orElse(null);
            entity.setShop(shop);
        }
        if (dto.getBranchId() != null) {
            Branch branch = branchRepo.findById(dto.getBranchId()).orElse(null);
            entity.setBranch(branch);
        }
        entity.setEntryUser(userId);
        baseUtils.setEntryUserInfo(entity);
        return modelMapper.map(shopInventoryItemRepo.save(entity), ShopInventoryItemDto.class);
    }

    @Override
    public ShopInventoryItemDto update(ShopInventoryItemDto dto, Long userId) {
        ShopInventoryItem entity = shopInventoryItemRepo.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Inventory item not found"));
        modelMapper.map(dto, entity);
        if (dto.getCatalogProductId() != null) {
            CatalogProduct catalogProduct = catalogProductRepo.findById(dto.getCatalogProductId()).orElse(null);
            entity.setCatalogProduct(catalogProduct);
        }
        if (dto.getShopId() != null) {
            Shop shop = shopRepo.findById(dto.getShopId()).orElse(null);
            entity.setShop(shop);
        }
        if (dto.getBranchId() != null) {
            Branch branch = branchRepo.findById(dto.getBranchId()).orElse(null);
            entity.setBranch(branch);
        }
        entity.setUpdateUser(userId);
        baseUtils.setUpdateUserInfo(entity, entity);
        return modelMapper.map(shopInventoryItemRepo.save(entity), ShopInventoryItemDto.class);
    }

    @Override
    public boolean delete(ShopInventoryItemDto dto, Long userId) {
        if (dto.getId() != null) {
            shopInventoryItemRepo.deleteById(dto.getId());
            return true;
        }
        return false;
    }

    @Override
    public ShopInventoryItemDto getById(Long id, Long userId) {
        return shopInventoryItemRepo.findById(id).map(entity -> modelMapper.map(entity, ShopInventoryItemDto.class)).orElse(null);
    }

    @Override
    public List<ShopInventoryItemDto> getAll(Long userId) {
        return shopInventoryItemRepo.findAll().stream().map(entity -> modelMapper.map(entity, ShopInventoryItemDto.class)).collect(Collectors.toList());
    }

    @Override
    public Page<ShopInventoryItemDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(), pageableBodyRequest.getSize());
        return shopInventoryItemRepo.findAll(pageRequest).map(entity -> modelMapper.map(entity, ShopInventoryItemDto.class));
    }
}
