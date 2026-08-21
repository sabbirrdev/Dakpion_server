package com.company.efood.sys.services;

import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.entity.UserRoleAssignDetails;
import com.company.efood.sys.model.CustomUserDetails;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.repository.UserRoleAssignDetailsRepo;
import com.company.efood.sys.repository.UserRoleAssignMasterRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    AppUserRepo appUserRepo;

   private UserRoleAssignMasterRepo userRoleAssignMasterRepo;
   private UserRoleAssignDetailsRepo userRoleAssignDetailsRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AppUser appUser = appUserRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User does't Exist"));

        if (!appUser.getActive()) {
            System.out.println("User is not active with username: " + username);
            throw new InternalAuthenticationServiceException("User is not active with username: " + username);
        }

        if (appUser.getAccountExpired()) {
            System.out.println("User is expired with username: " + username);
            throw new InternalAuthenticationServiceException("User is expired with username: " + username);
        }

        if (appUser.getAccountLocked()) {
            System.out.println("User is locked with username: " + username);
            throw new InternalAuthenticationServiceException("User is locked with username: " + username);
        }

        if (appUser.getCredentialsExpired()) {
            System.out.println("User credentials expired with username: " + username);
            throw new InternalAuthenticationServiceException("User credentials expired with username: " + username);
        }


        List<GrantedAuthority> authorities = new ArrayList<>();
        List<UserRoleAssignDetails> userRoleAssignDetails = userRoleAssignDetailsRepo.findByMasterAppUserId(appUser.getId());
        for (UserRoleAssignDetails userRoleAssignDetail : userRoleAssignDetails) {

            authorities.add(new SimpleGrantedAuthority(userRoleAssignDetail.getUserRole().getName()));
        }




        return CustomUserDetails.build(appUser);
    }

}


