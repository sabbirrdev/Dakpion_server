package com.company.efood.sys.repository;

import com.company.efood.sys.entity.UserRoleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleDetailsRepo extends JpaRepository<UserRoleDetails, Long> {
    List<UserRoleDetails> findByMasterId(Long id);

    //List<UserRoleDetails> findByMasterIdAndMenuItemMenuTypeAndMenuItemParentIdOrderByMenuItemSerialNoAsc(int roleId, int menuType, int moduleId);
}
