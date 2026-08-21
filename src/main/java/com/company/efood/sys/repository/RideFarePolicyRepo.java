package com.company.efood.sys.repository;

import com.company.efood.sys.entity.RideFarePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideFarePolicyRepo extends JpaRepository<RideFarePolicy, Long> {
    Optional<RideFarePolicy> findFirstByVehicleTypeIgnoreCaseAndActiveTrue(String vehicleType);
}
