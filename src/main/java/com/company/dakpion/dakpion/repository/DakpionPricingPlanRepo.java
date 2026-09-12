package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionPricingPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionPricingPlanRepo extends JpaRepository<DakpionPricingPlanEntity, String> {
    List<DakpionPricingPlanEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
