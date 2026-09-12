package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionTestimonialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionTestimonialRepo extends JpaRepository<DakpionTestimonialEntity, String> {
    List<DakpionTestimonialEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
