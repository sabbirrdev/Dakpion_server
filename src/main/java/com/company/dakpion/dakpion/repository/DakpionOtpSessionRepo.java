package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionOtpSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DakpionOtpSessionRepo extends JpaRepository<DakpionOtpSessionEntity, String> {

    Optional<DakpionOtpSessionEntity> findTopByPhoneOrderByCreatedAtDesc(String phone);

    Optional<DakpionOtpSessionEntity> findByVerificationToken(String verificationToken);

    long countByPhoneAndCreatedAtAfter(String phone, LocalDateTime after);

    void deleteAllByExpiresAtBefore(LocalDateTime before);
}
