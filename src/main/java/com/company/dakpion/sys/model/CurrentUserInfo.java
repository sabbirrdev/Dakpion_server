package com.company.dakpion.sys.model;

import com.company.dakpion.sys.utils.AppUserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrentUserInfo {

    private Long userId;           // Unique ID from AppUser
    private String username;          // Username of the user
    private AppUserType role;         // SELLER, CUSTOMER, ADMIN, etc.
    private Long referenceId;      // Role-specific ID (e.g., Seller.id, Raider.id)
    private Long userTypeId;       // Optional: map to a lookup table if needed
    private String displayName;       // Optional: show user-friendly name

}