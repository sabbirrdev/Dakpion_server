package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DakpionLetterRepo extends JpaRepository<DakpionLetterEntity, UUID> {

    Optional<DakpionLetterEntity> findByIdempotencyKey(String idempotencyKey);

    Page<DakpionLetterEntity> findAllByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);

    Page<DakpionLetterEntity> findAllByStatus(LetterStatus status, Pageable pageable);

    long countBySenderPhoneHashed(String senderPhoneHashed);

    List<DakpionLetterEntity> findAllByRecipientPhoneOrderByCreatedAtDesc(String recipientPhone);

    Page<DakpionLetterEntity> findAllByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId, Pageable pageable);

    Page<DakpionLetterEntity> findAllByRecipientPhoneOrRecipientUserIdOrderByCreatedAtDesc(String recipientPhone, Long recipientUserId, Pageable pageable);

    Page<DakpionLetterEntity> findAllBySenderPhoneHashedOrderByCreatedAtDesc(String senderPhoneHashed, Pageable pageable);

    Optional<DakpionLetterEntity> findByShortCode(String shortCode);

    Optional<DakpionLetterEntity> findByTrackingCode(String trackingCode);

    boolean existsByShortCode(String shortCode);

    boolean existsByTrackingCode(String trackingCode);

    Optional<DakpionLetterEntity> findByCourierTrackingId(String courierTrackingId);
}
