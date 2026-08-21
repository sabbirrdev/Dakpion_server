package com.company.efood.sys.repository;

import com.company.efood.sys.entity.UserRoleAssignMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleAssignMasterRepo extends JpaRepository<UserRoleAssignMaster, Long> {

}
