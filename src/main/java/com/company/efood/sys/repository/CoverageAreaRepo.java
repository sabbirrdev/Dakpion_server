package com.company.efood.sys.repository;

import com.company.efood.sys.entity.CoverageArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoverageAreaRepo extends JpaRepository<CoverageArea, Long> {
    List<CoverageArea> findByActiveTrue();
}
