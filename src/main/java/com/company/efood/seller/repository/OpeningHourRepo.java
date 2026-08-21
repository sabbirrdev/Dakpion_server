package com.company.efood.seller.repository;

import com.company.efood.sys.entity.OpeningHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpeningHourRepo extends JpaRepository<OpeningHour,Integer> {
}
