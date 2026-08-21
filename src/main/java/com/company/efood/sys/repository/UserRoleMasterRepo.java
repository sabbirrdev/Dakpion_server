package com.company.efood.sys.repository;

import com.company.efood.sys.entity.UserRoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleMasterRepo extends JpaRepository<UserRoleMaster, Long> {

}
