package com.company.efood.user.repository;

import com.company.efood.sys.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem,Long> {

    List<OrderItem> findByOrderId(Long customerId);
}
