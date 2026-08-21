package com.company.efood.sys.repository;

import com.company.efood.sys.entity.ExclusiveOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExclusiveOfferRepo extends JpaRepository<ExclusiveOffer, Long> {

    List<ExclusiveOffer> findByIsActiveTrue();

    Optional<ExclusiveOffer> findByCodeIgnoreCase(String code);

    Page<ExclusiveOffer> findAllByOrderByIdDesc(Pageable pageable);
}
