package com.company.efood.sys.repository;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.sys.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    String dropdownQuery = "SELECT a.id, " +
            "a.category_name AS name " +
            "FROM category a " +
            "WHERE 1 = 1 " +
            "AND a.active = true " +
            "ORDER BY a.category_name ASC";

    @Query(value = dropdownQuery, nativeQuery = true)
    List<BaseDropdownModel> findDropdownModel();

    String subCategoryQuery = "SELECT a.id," +
            "a.category_name AS name " +
            "FROM category a " +
            "WHERE a.active = true " +
            "AND a.parent_id = :parentId " +
            "ORDER BY a.category_name ASC";

    @Query(value = subCategoryQuery, nativeQuery = true)
    List<BaseDropdownModel> findSubcategoryDropdownModel(@Param("parentId") Long parentId);

    Page<Category> findByParentCategoryIsNull(Pageable pageable);

    Page<Category> findByCategoryNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Category> findByCategoryNameContainingIgnoreCaseAndActive(String name, Boolean active, Pageable pageable);

    Page<Category> findByActive(Boolean active, Pageable pageable);

    Optional<Category> findByCategoryName(String categoryName);
}
