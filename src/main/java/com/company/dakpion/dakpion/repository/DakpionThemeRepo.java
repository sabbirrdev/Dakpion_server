package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionThemeRepo extends JpaRepository<DakpionThemeEntity, String> {
    List<DakpionThemeEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
