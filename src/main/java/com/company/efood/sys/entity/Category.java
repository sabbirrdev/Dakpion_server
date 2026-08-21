package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "CATEGORY")
public class Category extends BaseEntity {

    @Column(name = "category_name",unique = true,nullable = false)
    private String categoryName;

    @Column(name = "category_name_bangla")
    private String categoryNameBangla;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> productList = new ArrayList<>();
}
