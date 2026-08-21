package com.company.efood.sys.repository;

import com.company.efood.sys.entity.UserRoleAssignDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleAssignDetailsRepo extends JpaRepository<UserRoleAssignDetails, Long> {
    List<UserRoleAssignDetails> findByMasterId(Long Id);

    List<UserRoleAssignDetails> findByMasterAppUserId(Long appUserId);
}
