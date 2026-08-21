package com.company.efood.sys.repository;

import com.company.efood.sys.entity.PasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PasswordPolicyRepo extends JpaRepository<PasswordPolicy, Long> {

    Optional<PasswordPolicy> findById(Long id);

} 
