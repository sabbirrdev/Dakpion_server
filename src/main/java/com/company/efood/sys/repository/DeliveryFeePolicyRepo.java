package com.company.efood.sys.repository;

import com.company.efood.sys.entity.DeliveryFeePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryFeePolicyRepo extends JpaRepository<DeliveryFeePolicy,Long> {
}
