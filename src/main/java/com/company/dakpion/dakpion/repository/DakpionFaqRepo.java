package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionFaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionFaqRepo extends JpaRepository<DakpionFaqEntity, String> {
    List<DakpionFaqEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
