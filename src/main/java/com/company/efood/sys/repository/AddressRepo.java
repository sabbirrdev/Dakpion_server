package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Address;
import com.company.efood.sys.utils.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {

    /** All saved addresses for a given user (entry-user = appUser.id). */
    List<Address> findByEntryUserAndActiveTrue(Long entryUser);

    /** Addresses of a specific type for a given user. */
    List<Address> findByEntryUserAndAddressTypeAndActiveTrue(Long entryUser, AddressType addressType);
}
