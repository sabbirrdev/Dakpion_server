package com.company.dakpion.sys.services;

import com.company.dakpion.sys.entity.AppUser;
import com.company.dakpion.sys.model.CustomUserDetails;
import com.company.dakpion.sys.repository.AppUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepo appUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AppUser appUser = appUserRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User does not exist with username: " + username));

        if (Boolean.FALSE.equals(appUser.getActive())) {
            throw new InternalAuthenticationServiceException("User is not active with username: " + username);
        }

        if (Boolean.TRUE.equals(appUser.getAccountExpired())) {
            throw new InternalAuthenticationServiceException("User is expired with username: " + username);
        }

        if (Boolean.TRUE.equals(appUser.getAccountLocked())) {
            throw new InternalAuthenticationServiceException("User is locked with username: " + username);
        }

        if (Boolean.TRUE.equals(appUser.getCredentialsExpired())) {
            throw new InternalAuthenticationServiceException("User credentials expired with username: " + username);
        }

        return CustomUserDetails.build(appUser);
    }
}
