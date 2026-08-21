package com.company.efood.catalog.service.impl;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.catalog.dto.CatalogProductDto;
import com.company.efood.catalog.entity.CatalogProduct;
import com.company.efood.catalog.repository.CatalogProductRepo;
import com.company.efood.catalog.service.CatalogProductService;
import com.company.efood.sys.entity.Category;
import com.company.efood.sys.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CatalogProductServiceImpl implements CatalogProductService {

    private final CatalogProductRepo catalogProductRepo;
    private final CategoryRepo categoryRepo;
    private final BaseUtils baseUtils;
    private final ModelMapper modelMapper;

    @Override
    public CatalogProductDto save(CatalogProductDto dto, Long userId) {
        CatalogProduct entity = modelMapper.map(dto, CatalogProduct.class);
        if (dto.getCategoryId() != null) {
            Category category = categoryRepo.findById(dto.getCategoryId()).orElse(null);
            entity.setCategory(category);
        }
        entity.setEntryUser(userId);
        baseUtils.setEntryUserInfo(entity);
        CatalogProduct saved = catalogProductRepo.save(entity);
        return modelMapper.map(saved, CatalogProductDto.class);
    }

    @Override
    public CatalogProductDto update(CatalogProductDto dto, Long userId) {
        CatalogProduct entity = catalogProductRepo.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Catalog product not found"));
        modelMapper.map(dto, entity);
        if (dto.getCategoryId() != null) {
            Category category = categoryRepo.findById(dto.getCategoryId()).orElse(null);
            entity.setCategory(category);
        }
        entity.setUpdateUser(userId);
        baseUtils.setUpdateUserInfo(entity, entity);
        return modelMapper.map(catalogProductRepo.save(entity), CatalogProductDto.class);
    }

    @Override
    public boolean delete(CatalogProductDto dto, Long userId) {
        if (dto.getId() != null) {
            catalogProductRepo.deleteById(dto.getId());
            return true;
        }
        return false;
    }

    @Override
    public CatalogProductDto getById(Long id, Long userId) {
        return catalogProductRepo.findById(id).map(entity -> modelMapper.map(entity, CatalogProductDto.class)).orElse(null);
    }

    @Override
    public List<CatalogProductDto> getAll(Long userId) {
        return catalogProductRepo.findAll().stream().map(entity -> modelMapper.map(entity, CatalogProductDto.class)).collect(Collectors.toList());
    }

    @Override
    public Page<CatalogProductDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(), pageableBodyRequest.getSize());
        return catalogProductRepo.findAll(pageRequest).map(entity -> modelMapper.map(entity, CatalogProductDto.class));
    }
}
