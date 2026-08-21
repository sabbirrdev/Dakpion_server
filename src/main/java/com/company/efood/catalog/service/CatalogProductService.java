package com.company.efood.catalog.service;

import com.company.efood.catalog.dto.CatalogProductDto;
import com.company.efood.base.BasePageableRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CatalogProductService {
    CatalogProductDto save(CatalogProductDto dto, Long userId);
    CatalogProductDto update(CatalogProductDto dto, Long userId);
    boolean delete(CatalogProductDto dto, Long userId);
    CatalogProductDto getById(Long id, Long userId);
    List<CatalogProductDto> getAll(Long userId);
    Page<CatalogProductDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId);
}
