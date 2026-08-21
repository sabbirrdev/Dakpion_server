package com.company.efood.sys.config;

import com.company.efood.sys.entity.Category;
import com.company.efood.sys.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategorySeedInitializer implements ApplicationRunner {

    private final CategoryRepo categoryRepo;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        syncCategoryIdentitySequence();

        Map<String, Category> parentCategories = new LinkedHashMap<>();
        parentCategories.put("Food", ensureRootCategory("Food", "খাবার", "Food delivery category"));
        parentCategories.put("Grocery", ensureRootCategory("Grocery", "মুদির দোকান", "Daily grocery category"));
        parentCategories.put("Vegetables", ensureRootCategory("Vegetables", "সবজি", "Vegetable items category"));
        parentCategories.put("Medicine", ensureRootCategory("Medicine", "ওষুধ", "Medicine and pharmacy category"));
        parentCategories.put("Ride Service", ensureRootCategory("Ride Service", "রাইড সার্ভিস", "Ride-sharing and transport category"));

        createChildCategory(parentCategories.get("Food"), "Burger", "বার্গার", "Burger items");
        createChildCategory(parentCategories.get("Food"), "Pizza", "পিজা", "Pizza items");
        createChildCategory(parentCategories.get("Food"), "Fast Food", "ফাস্ট ফুড", "Fast food items");

        createChildCategory(parentCategories.get("Grocery"), "Rice", "চাল", "Rice grocery product");
        createChildCategory(parentCategories.get("Grocery"), "Oil", "তেল", "Cooking oil items");
        createChildCategory(parentCategories.get("Grocery"), "Household", "গৃহস্থালি", "Household essentials");

        createChildCategory(parentCategories.get("Vegetables"), "Leafy Greens", "পাতা শাক", "Leafy green vegetables");
        createChildCategory(parentCategories.get("Vegetables"), "Root Vegetables", "মূল সবজি", "Root vegetables");

        createChildCategory(parentCategories.get("Medicine"), "Prescription", "প্রেসক্রিপশন", "Prescription medicine");
        createChildCategory(parentCategories.get("Medicine"), "OTC", "ওটিসি", "Over-the-counter medicine");

        createChildCategory(parentCategories.get("Ride Service"), "Motorcycle", "মোটরসাইকেল", "Motorcycle ride-sharing");
        createChildCategory(parentCategories.get("Ride Service"), "Auto Rickshaw", "অটো রিকশা", "Auto-rickshaw ride-sharing");
        createChildCategory(parentCategories.get("Ride Service"), "CNG", "সিএনজি", "CNG ride-sharing");
        createChildCategory(parentCategories.get("Ride Service"), "Van", "ভ্যান", "Van ride-sharing");
        createChildCategory(parentCategories.get("Ride Service"), "Car", "কার", "Car ride-sharing");
    }

    private void syncCategoryIdentitySequence() {
        jdbcTemplate.queryForObject(
                "SELECT setval(pg_get_serial_sequence('category', 'id'), COALESCE((SELECT MAX(id) + 1 FROM category), 1), false)",
                Long.class
        );
    }

    private Category ensureRootCategory(String categoryName, String banglaName, String description) {
        return categoryRepo.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepo.save(createRootCategory(categoryName, banglaName, description)));
    }

    private Category createRootCategory(String categoryName, String banglaName, String description) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCategoryNameBangla(banglaName);
        category.setDescription(description);
        category.setActive(true);
        category.setEntryUser(0L);
        category.setEntryDate(LocalDateTime.now());
        return category;
    }

    private void createChildCategory(Category parent, String categoryName, String banglaName, String description) {
        if (parent == null || categoryRepo.findByCategoryName(categoryName).isPresent()) {
            return;
        }

        Category child = new Category();
        child.setCategoryName(categoryName);
        child.setCategoryNameBangla(banglaName);
        child.setDescription(description);
        child.setParentCategory(parent);
        child.setActive(true);
        child.setEntryUser(0L);
        child.setEntryDate(LocalDateTime.now());
        categoryRepo.save(child);
    }
}
