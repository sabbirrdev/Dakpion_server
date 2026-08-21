package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Long> {

    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(Long orderId);
}
