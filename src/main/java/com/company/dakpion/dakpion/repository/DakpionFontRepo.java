package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionFontEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionFontRepo extends JpaRepository<DakpionFontEntity, String> {
    List<DakpionFontEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
    List<DakpionFontEntity> findAllByOrderByDisplayOrderAsc();
}
