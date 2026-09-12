package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.constant.SmsStatus;
import com.company.dakpion.dakpion.entity.DakpionSmsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DakpionSmsLogRepo extends JpaRepository<DakpionSmsLogEntity, Long> {

    List<DakpionSmsLogEntity> findByLetterIdAndStatus(UUID letterId, SmsStatus status);

    /**
     * Find all SMS logs that are still in a non-terminal state and were created within the last 24h.
     */
    List<DakpionSmsLogEntity> findByStatusInAndCreatedAtAfter(
            List<SmsStatus> statuses, LocalDateTime after);
}
