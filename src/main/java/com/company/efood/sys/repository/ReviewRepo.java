package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review,Long> {
}
