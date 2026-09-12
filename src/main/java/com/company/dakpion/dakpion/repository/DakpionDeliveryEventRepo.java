package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DakpionDeliveryEventRepo extends JpaRepository<DakpionDeliveryEventEntity, Long> {

    List<DakpionDeliveryEventEntity> findByLetterIdOrderByOccurredAtAsc(UUID letterId);
}
