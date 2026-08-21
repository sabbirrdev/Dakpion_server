package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepo extends JpaRepository<Commission, Long> {
    List<Commission> findByOrderId(Long orderId);
    List<Commission> findByShopId(Long shopId);
}
