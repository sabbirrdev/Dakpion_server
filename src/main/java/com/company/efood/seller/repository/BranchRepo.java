package com.company.efood.seller.repository;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.sys.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepo extends JpaRepository<Branch,Long> {

    @Query("SELECT b.id AS id, b.name AS name " +
            "FROM Branch b " +
            "WHERE b.shop.id = :shopId " +
            "AND b.active = true " +
            "ORDER BY b.name ASC")
    List<BaseDropdownModel> findDropdownModelByShopId(@Param("shopId") Long shopId);
}
