package com.company.efood.user.repository;

import com.company.efood.user.entity.LoyaltyAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyAccountRepo extends JpaRepository<LoyaltyAccount, Long> {

    Optional<LoyaltyAccount> findByCustomerId(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT la FROM LoyaltyAccount la WHERE la.customer.id = :customerId")
    Optional<LoyaltyAccount> findByCustomerIdWithLock(@Param("customerId") Long customerId);

    @Query("SELECT la FROM LoyaltyAccount la WHERE la.customer.appUser.id = :appUserId")
    Optional<LoyaltyAccount> findByCustomerAppUserId(@Param("appUserId") Long appUserId);
}
