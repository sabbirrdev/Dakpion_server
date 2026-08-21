package com.company.efood.sys.repository;

import com.company.efood.sys.entity.RideRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestRepo extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByCustomerIdOrderByIdDesc(Long customerId);
    Page<RideRequest> findByCustomerId(Long customerId, Pageable pageable);
    List<RideRequest> findByStatusOrderByIdDesc(String status);
    List<RideRequest> findByRaiderIdOrderByIdDesc(Long raiderId);
}
