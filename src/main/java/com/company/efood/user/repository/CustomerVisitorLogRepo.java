package com.company.efood.user.repository;

import com.company.efood.user.entity.CustomerVisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerVisitorLogRepo extends JpaRepository<CustomerVisitorLog, Long> {

    Optional<CustomerVisitorLog> findByVisitorUuid(String visitorUuid);

    @Query("SELECT COUNT(DISTINCT c.visitorUuid) FROM CustomerVisitorLog c")
    long countDistinctVisitors();
}
