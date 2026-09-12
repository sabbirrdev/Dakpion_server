package com.company.dakpion.sys.repository;

import com.company.dakpion.sys.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    AppUser findByUsernameAndActive(String username, Boolean active);

    List<AppUser> findByActive(boolean active);
}
