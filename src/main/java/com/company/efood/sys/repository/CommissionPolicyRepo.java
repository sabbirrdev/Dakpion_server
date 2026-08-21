package com.company.efood.sys.repository;

import com.company.efood.sys.entity.CommissionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionPolicyRepo extends JpaRepository<CommissionPolicy,Long> {
}
