package com.company.efood.sys.services.serviceimpl;

import com.company.efood.raider.entity.Raider;
import com.company.efood.raider.repository.RaiderRepo;
import com.company.efood.seller.entity.Seller;
import com.company.efood.seller.repository.SellerRepo;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.services.RegisterService;
import com.company.efood.sys.utils.AppUserType;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.repository.CustomerRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private RaiderRepo raiderRepo;
    @Autowired
    private CustomerRepo customerRepo;


    @Override
    @Transactional
    public AppUser addUser(AppUser appUser){
        System.out.println(appUser);
        AppUser savedEntity = appUserRepo.save(appUser);
        System.out.println("APP USER SAVED");
        Map<AppUserType, Consumer<AppUser>> userTypeHandlers = Map.of(
                AppUserType.CUSTOMER, user -> customerRepo.save(new Customer(user)),
                AppUserType.SELLER, user -> sellerRepo.save(new Seller(user)),
                AppUserType.RAIDER, user -> raiderRepo.save(new Raider(user))
        );
        userTypeHandlers.getOrDefault(appUser.getAppUserType(), user -> {}).accept(savedEntity);
        return savedEntity;
    }

    @Override
    @Transactional
    public Optional<AppUser> getUserByUsername(String userName) {
        return appUserRepo.findByUsername(userName);
    }


}
