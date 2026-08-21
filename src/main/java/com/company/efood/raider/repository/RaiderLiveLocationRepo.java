package com.company.efood.raider.repository;

import com.company.efood.raider.entity.Raider;
import com.company.efood.sys.entity.RaiderLiveLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaiderLiveLocationRepo extends JpaRepository<RaiderLiveLocation, Long> {
    Optional<RaiderLiveLocation> findByRaiderId(Long raiderId);
    Optional<RaiderLiveLocation> findByRaider(Raider raider);
}
