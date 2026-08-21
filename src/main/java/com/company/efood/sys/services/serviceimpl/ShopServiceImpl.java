package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.seller.entity.Seller;
import com.company.efood.seller.repository.SellerRepo;
import com.company.efood.sys.dto.AddressDto;
import com.company.efood.sys.dto.ShopDto;
import com.company.efood.sys.entity.*;
import com.company.efood.sys.model.UserRoleModel;
import com.company.efood.sys.repository.ShopRepo;
import com.company.efood.sys.services.ShopService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ShopServiceImpl implements ShopService {
    private ShopRepo shopRepo;
    private SellerRepo sellerRepo;
    private BaseUtils baseUtils;
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ShopDto save(ShopDto obj, Long userId) {
        Long sellerId = CurrentUserContext.getReferenceId();
        Shop shopEntity = shopRepo.save(generateEntity(obj,userId,sellerId,true));
        return generateDto(shopEntity);
    }

    @Override
    @Transactional
    public ShopDto update(ShopDto obj, Long userId) {
        Long sellerId = CurrentUserContext.getReferenceId();
        Shop existing = shopRepo.findById(obj.getId()).orElseThrow(() -> new RuntimeException("Shop not found"));
        Shop updated = generateEntity(obj, userId, sellerId, false);
        updated.setId(existing.getId());
        updated.setEntryUser(existing.getEntryUser());
        updated.setEntryDate(existing.getEntryDate());
        return generateDto(shopRepo.save(updated));
    }

    @Override
    @Transactional
    public boolean delete(ShopDto obj, Long userId) {
        if (obj.getId() != null) {
            shopRepo.deleteById(obj.getId());
            return true;
        }
        return false;
    }

    @Override
    public ShopDto getById(Long id, Long userId) {
        return shopRepo.findById(id).map(this::generateDto).orElse(null);
    }

    @Override
    public ShopDto getMyShop(Long userId) {
        Long sellerId = CurrentUserContext.getReferenceId();
        if (sellerId == null) return null;
        return shopRepo.findShopBySellerId(sellerId).map(this::generateDto).orElse(null);
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return shopRepo.findAll().stream().map(shop -> new BaseDropdownModel() {
            @Override
            public Integer getId() {
                return Math.toIntExact(shop.getId());
            }

            @Override
            public String getName() {
                return shop.getShopName();
            }

            @Override
            public Integer getExtra() {
                return null;
            }

            @Override
            public String getExtraName() {
                return null;
            }

            @Override
            public String getExtraFromDate() {
                return null;
            }

            @Override
            public String getExtraToDate() {
                return null;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Page<ShopDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        PageRequest pageRequest = baseUtils.getPageRequest(pageableBodyRequest.getPage(),pageableBodyRequest.getSize());
        Page<Shop> shopPage = shopRepo.findAll(pageRequest);
        List<ShopDto> shopList = convertEntityListToDtoList(shopPage.getContent().stream());
        return new PageImpl<>(shopList,pageRequest,shopPage.getTotalElements());
    }

    //-----------------------Helper Function---------------------------

    private Shop generateEntity(ShopDto dto, Long userId, Long sellerId, Boolean isSaved) {
        Shop entity = new Shop();

        // ১. সেফটি চেক: ডিটিও বা আইডি নাল কি না
        if (dto == null) {
            throw new RuntimeException("ShopDto is null");
        }

        BeanUtils.copyProperties(dto, entity);

        try {
            if (isSaved) {
                // নতুন শপ সেভ করার লজিক
                if (sellerId == null) {
                    throw new RuntimeException("sellerId can not be null for saving new shop");
                }
                Seller seller = sellerRepo.findById(sellerId)
                        .orElseThrow(() -> new NameNotFoundException("Seller Not Found with ID: " + sellerId));

                entity.setEntryUser(userId);
                entity.setSeller(seller);
                baseUtils.setEntryUserInfo(entity);
            } else {
                // পুরাতন শপ আপডেট করার লজিক
                if (dto.getId() == null) {
                    throw new RuntimeException("Shop ID in DTO cannot be null for update");
                }
                Shop dbEntity = shopRepo.findById(dto.getId())
                        .orElseThrow(() -> new NameNotFoundException("Shop not found with ID: " + dto.getId()));

                entity.setUpdateUser(userId);
                baseUtils.setUpdateUserInfo(entity, dbEntity);
            }

            return entity;
        } catch (Exception e) {
            // এই লাইনটি আপনার কনসোলে আসল সমস্যা (Root Cause) লাল কালিতে প্রিন্ট করে দেবে
            e.printStackTrace();
            throw new RuntimeException("Error generating Shop entity. Root cause: " + e.getMessage(), e);
        }
    }


    private Address generateAddressEntity(AddressDto addressDto,Long userId) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        address.setEntryDate(LocalDateTime.now());
        address.setEntryUser(userId);
        System.out.println(address);
        return address;
    }

    private List<ShopDto> convertEntityListToDtoList(Stream<Shop> entityList) {
        return entityList.map(entity -> {
            return generateDto(entity);
        }).collect(Collectors.toList());
    }


    private ShopDto generateDto(Shop entity) {
        ShopDto dto = modelMapper.map(entity, ShopDto.class);
        return dto;
    }


}
