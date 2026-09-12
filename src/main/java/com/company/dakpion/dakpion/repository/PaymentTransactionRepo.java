package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionPaymentTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepo extends JpaRepository<DakpionPaymentTransactionEntity, Long> {
    Optional<DakpionPaymentTransactionEntity> findByTransactionId(String transactionId);
    Optional<DakpionPaymentTransactionEntity> findFirstByLetterIdOrderByCreatedAtDesc(UUID letterId);
}
