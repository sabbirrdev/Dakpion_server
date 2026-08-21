package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepo extends JpaRepository<Coupon,Long> {

    Optional<Coupon> findByCode(String code);

}
