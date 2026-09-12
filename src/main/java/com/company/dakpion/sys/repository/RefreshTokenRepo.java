package com.company.dakpion.sys.repository;

import com.company.dakpion.sys.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, String> {

    Optional<RefreshTokenEntity> findByTokenAndExpiresAtAfter(String token, LocalDateTime now);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpiresAtBefore(LocalDateTime before);
}
