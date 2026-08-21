package com.company.efood.zone.repository;

import com.company.efood.zone.entity.Upazila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpazilaRepository extends JpaRepository<Upazila, Long> {
    List<Upazila> findByDistrictId(Long districtId);
    List<Upazila> findByActiveTrue();
}
