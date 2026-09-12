package com.company.dakpion.dakpion.repository;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.entity.DakpionDeliveryOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DakpionDeliveryOptionRepo extends JpaRepository<DakpionDeliveryOptionEntity, DeliveryType> {
    List<DakpionDeliveryOptionEntity> findAllByActiveTrueOrderByDisplayOrderAsc();
}
