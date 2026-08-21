package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.CategoryDto;
import com.company.efood.sys.entity.Category;
import com.company.efood.sys.repository.CategoryRepo;
import com.company.efood.sys.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final BaseUtils baseUtils;

    @Override
    public CategoryDto save(CategoryDto obj, Long userId) {
        Category category = categoryRepo.save(generateEntity(obj, userId, true));
        return generateDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto obj, Long userId) {
        if (obj.getId() == null || !categoryRepo.existsById(obj.getId())) {
            throw new RuntimeException("Category not found");
        }
        Category updated = generateEntity(obj, userId, false);
        return generateDto(categoryRepo.save(updated));
    }

    @Override
    public boolean delete(CategoryDto obj, Long userId) {
        if (obj == null || obj.getId() == null || !categoryRepo.existsById(obj.getId())) {
            throw new RuntimeException("Category not found");
        }
        categoryRepo.deleteById(obj.getId());
        return true;
    }

    @Override
    public CategoryDto getById(Long id, Long userId) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        return generateDto(category);
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return categoryRepo.findDropdownModel();
    }

    @Override
    public List<BaseDropdownModel> getDropdownListByParentId(Long parentId, Long userId) {
        return categoryRepo.findSubcategoryDropdownModel(parentId);
    }

    @Override
    public Page<CategoryDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        int page = pageableBodyRequest.getPage() != null ? pageableBodyRequest.getPage() : 0;
        int size = pageableBodyRequest.getSize() != null && pageableBodyRequest.getSize() > 0 ? pageableBodyRequest.getSize() : 20;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        String search = pageableBodyRequest.getSearchValue();
        String activeParam = pageableBodyRequest.getStringParam1();

        Boolean activeFilter = null;
        if (activeParam != null && !activeParam.isBlank()) {
            activeFilter = Boolean.parseBoolean(activeParam);
        }

        Page<Category> categoryPage;
        if (search != null && !search.isBlank()) {
            if (activeFilter != null) {
                categoryPage = categoryRepo.findByCategoryNameContainingIgnoreCaseAndActive(search.trim(), activeFilter, pageRequest);
            } else {
                categoryPage = categoryRepo.findByCategoryNameContainingIgnoreCase(search.trim(), pageRequest);
            }
        } else if (activeFilter != null) {
            categoryPage = categoryRepo.findByActive(activeFilter, pageRequest);
        } else {
            categoryPage = categoryRepo.findAll(pageRequest);
        }

        List<CategoryDto> dtos = convertEntityListToDtoList(categoryPage.stream());
        return new PageImpl<>(dtos, pageRequest, categoryPage.getTotalElements());
    }

    // ─── Helper Functions ────────────────────────────────────────────────────────

    private Category generateEntity(CategoryDto dto, Long userId, Boolean isSaved) {
        Category entity = new Category();

        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            entity.setCategoryName(dto.getCategoryName().trim());
        } else if (dto.getName() != null && !dto.getName().isBlank()) {
            entity.setCategoryName(dto.getName().trim());
        }

        entity.setCategoryNameBangla(dto.getCategoryNameBangla());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Category parent = categoryRepo.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + dto.getParentId()));
            entity.setParentCategory(parent);
        } else {
            entity.setParentCategory(null);
        }

        if (isSaved) {
            entity.setEntryUser(userId);
            entity.setEntryDate(LocalDateTime.now());
            baseUtils.setEntryUserInfo(entity);
        } else {
            entity.setId(dto.getId());
            Category dbEntity = categoryRepo.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getId()));
            entity.setEntryUser(dbEntity.getEntryUser());
            entity.setEntryDate(dbEntity.getEntryDate());
            entity.setUpdateUser(userId);
            entity.setUpdateDate(LocalDateTime.now());
        }

        return entity;
    }

    private CategoryDto generateDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setCategoryName(entity.getCategoryName());
        dto.setCategoryNameBangla(entity.getCategoryNameBangla());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setEntryDate(entity.getEntryDate());

        if (entity.getParentCategory() != null) {
            dto.setParentId(entity.getParentCategory().getId());
            dto.setParentName(entity.getParentCategory().getCategoryName());
        } else {
            dto.setParentId(null);
            dto.setParentName(null);
        }
        return dto;
    }

    private List<CategoryDto> convertEntityListToDtoList(Stream<Category> entityList) {
        return entityList.map(this::generateDto).collect(Collectors.toList());
    }
}
