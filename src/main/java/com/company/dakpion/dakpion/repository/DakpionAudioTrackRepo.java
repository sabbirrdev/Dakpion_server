package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionAudioTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionAudioTrackRepo extends JpaRepository<DakpionAudioTrackEntity, String> {
    List<DakpionAudioTrackEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
