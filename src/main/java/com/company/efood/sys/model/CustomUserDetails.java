package com.company.efood.sys.model;

import com.company.efood.sys.utils.AppUserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.company.efood.sys.entity.AppUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;
    @Getter
    private final String username;
    @JsonIgnore
    private final String password;
    @Getter
    private final Integer userTypeId;
    @Getter
    private final AppUserType appUserType;
    @Getter
    private final Long referenceId;

    public CustomUserDetails(
            Long id,
            String username,
            String password,
            Integer userTypeId,
            AppUserType appUserType,
            Long referenceId
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userTypeId = userTypeId;
        this.appUserType = appUserType;
        this.referenceId = referenceId;
    }

    // Build method from AppUser
    public static CustomUserDetails build(AppUser user) {
        Long referenceId = null;

        switch (user.getAppUserType()) {
            case SELLER -> referenceId = user.getSeller() != null ? user.getSeller().getId() : null;
            case CUSTOMER -> referenceId = user.getCustomer() != null ? user.getCustomer().getId() : null;
            case SYSTEM_ADMIN -> referenceId = user.getAdmin() != null ? user.getAdmin().getId() : null;
            case RAIDER -> referenceId = user.getRaider() != null ? user.getRaider().getId() : null;
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserTypeId(),
                user.getAppUserType(),
                referenceId
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + appUserType.name())); // or return authorities later
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomUserDetails other = (CustomUserDetails) obj;
        return Objects.equals(id, other.id);
    }
}
