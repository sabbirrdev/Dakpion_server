package com.company.efood.sys.services;

import java.util.Optional;

import com.company.efood.sys.entity.AppUser;


public interface RegisterService {

    AppUser addUser(AppUser appUser);

    Optional<AppUser> getUserByUsername(String userName);
}
