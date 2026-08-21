package com.company.efood.user.repository;

import com.company.efood.user.entity.LoyaltyTransaction;
import com.company.efood.user.entity.LoyaltyTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyTransactionRepo extends JpaRepository<LoyaltyTransaction, Long> {

    Page<LoyaltyTransaction> findByCustomerIdOrderByEntryDateDesc(Long customerId, Pageable pageable);

    @Query("SELECT lt FROM LoyaltyTransaction lt WHERE lt.customer.appUser.id = :appUserId ORDER BY lt.entryDate DESC")
    Page<LoyaltyTransaction> findByCustomerAppUserIdOrderByEntryDateDesc(@Param("appUserId") Long appUserId, Pageable pageable);

    boolean existsByOrderIdAndType(Long orderId, LoyaltyTransactionType type);

    Optional<LoyaltyTransaction> findByOrderIdAndType(Long orderId, LoyaltyTransactionType type);

    List<LoyaltyTransaction> findByCustomerIdAndType(Long customerId, LoyaltyTransactionType type);

    long countByCustomerIdAndType(Long customerId, LoyaltyTransactionType type);

    @Query("SELECT lt FROM LoyaltyTransaction lt WHERE lt.type = 'EARNED' AND lt.entryDate < :cutoffDate")
    List<LoyaltyTransaction> findEarnedBeforeCutoff(@Param("cutoffDate") LocalDateTime cutoffDate);
}
