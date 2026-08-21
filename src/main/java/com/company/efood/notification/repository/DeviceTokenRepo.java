package com.company.efood.notification.repository;

import com.company.efood.notification.entity.DeviceToken;
import com.company.efood.sys.utils.AppUserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepo extends JpaRepository<DeviceToken, Long> {

    List<DeviceToken> findByAppUserIdAndActiveTrue(Long appUserId);

    List<DeviceToken> findByAppUserTypeAndActiveTrue(AppUserType appUserType);

    Optional<DeviceToken> findByFcmToken(String fcmToken);

    void deleteByFcmToken(String fcmToken);
}
