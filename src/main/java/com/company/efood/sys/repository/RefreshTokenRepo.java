package com.company.efood.sys.repository;

import com.company.efood.sys.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByRefreshToken(String token);

    Optional<RefreshToken> findByAppUserId(Integer Id);

}
