package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.entity.DakpionBlockedWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionBlockedWordRepo extends JpaRepository<DakpionBlockedWordEntity, Long> {
    List<DakpionBlockedWordEntity> findAllByActiveTrue();
}
