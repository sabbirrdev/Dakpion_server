package com.company.efood.zone.repository;

import com.company.efood.zone.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByUpazilaId(Long upazilaId);
    List<Zone> findByActiveTrue();
    List<Zone> findByUpazilaDistrictId(Long districtId);
    List<Zone> findByUpazilaDistrictDivisionId(Long divisionId);
}
