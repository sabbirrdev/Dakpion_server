package com.company.efood.user.repository;

import com.company.efood.user.entity.CartItem;
import com.company.efood.user.model.CartSummaryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartKey(String cartKey);
    List<CartItem> findByCustomerId(Long customerId);
    java.util.Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    java.util.Optional<CartItem> findByCartKeyAndProductId(String cartKey, Long productId);

    @Query(value = "SELECT c.* FROM cart_item c  WHERE c.cart_key = :cartKey ", nativeQuery = true)
    Page<CartItem> pageableCartByCartKey(String cartKey, Pageable pageable);
    @Query(value = "SELECT c.* FROM cart_item c JOIN customer cu ON cu.id = c.customer_id WHERE  cu.id = :customerId", nativeQuery = true)
    Page<CartItem> pageableCartByCustomerId(Long customerId, Pageable pageable);

    void deleteByCartKey(String cartKey);
    void deleteByCustomerId(Long customerId);

    @Query(value = "SELECT * FROM get_cart_summary(:customerId, :cartKey, :couponCode)", nativeQuery = true)
    CartSummaryModel getCartSummary(@Param("customerId") Long customerId, @Param("cartKey") String cartKey, @Param("couponCode") String couponCode);
}
