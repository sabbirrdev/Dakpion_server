package com.company.efood.user.services.servicesimpl;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.dto.ProductDto;
import com.company.efood.sys.entity.Product;
import com.company.efood.sys.repository.CouponRepo;
import com.company.efood.sys.repository.ProductRepo;
import com.company.efood.user.dto.CartItemDto;
import com.company.efood.user.entity.CartItem;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.model.CartSummaryModel;
import com.company.efood.user.repository.CartRepo;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.services.CartService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final CustomerRepo customerRepo;
    private final CouponRepo couponRepo;
    private BaseUtils baseUtils;

    @Override
    public CartItemDto addToCart(CartItemDto dto) {
        final CartItem entity = cartRepo.save(generateEntity(dto, true));
        return generateDto(entity);
    }

    @Override
    public CartItemDto removeItem(CartItemDto cartItemDto) {
        if (cartItemDto != null && cartItemDto.getId() != null) {
            cartRepo.deleteById(cartItemDto.getId());
        }
        return cartItemDto;
    }

    @Override
    public Page<CartItemDto> getPageableCartItems(BasePageableRequest basePageableRequest) {
        Long customerId = CurrentUserContext.getReferenceId();
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());
        Page<CartItem> cartItemPage = (customerId != null) ? cartRepo.pageableCartByCustomerId(customerId, pageRequest) : cartRepo.pageableCartByCartKey(basePageableRequest.getStringParam1(), pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(cartItemPage.stream()), pageRequest, cartItemPage.getTotalElements());
    }

    @Override
    public void clearCart(String cartKey, Long customerId) {
        if (customerId != null) {
            cartRepo.deleteByCustomerId(customerId);
        } else {
            cartRepo.deleteByCartKey(cartKey);
        }
    }

    @Transactional
    @Override
    public void mergeGuestCartToUser(String cartKey, Long userId) {
        Logger logger = Logger.getLogger(CartServiceImpl.class.getName());
        logger.info("Merging guest cart to user: " + cartKey + " " + userId);
        logger.info("CustomerId: " + CurrentUserContext.getReferenceId() + " " + CurrentUserContext.getRole());
        Customer customer = customerRepo.findById(Objects.requireNonNull(CurrentUserContext.getReferenceId())).orElseThrow(() -> new RuntimeException("Customer not found"));

        List<CartItem> guestItems = cartRepo.findByCartKey(cartKey);
        if (guestItems.isEmpty()) return;

        List<CartItem> userItems = cartRepo.findByCustomerId(customer.getId());

        Map<Long, CartItem> userCartMap = userItems.stream().collect(Collectors.toMap(item -> item.getProduct().getId(), Function.identity()));

        for (CartItem guestItem : guestItems) {
            Long productId = guestItem.getProduct().getId();

            BigDecimal unitPrice = guestItem.getUnitPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
                Product dbProduct = productRepo.findById(productId).orElse(null);
                if (dbProduct != null) {
                    unitPrice = dbProduct.getDiscountPrice() != null
                            ? dbProduct.getDiscountPrice()
                            : (dbProduct.getPrice() != null ? dbProduct.getPrice() : BigDecimal.ZERO);
                } else {
                    unitPrice = BigDecimal.ZERO;
                }
            }

            if (userCartMap.containsKey(productId)) {
                CartItem userItem = userCartMap.get(productId);
                int newQty = userItem.getQuantity() + guestItem.getQuantity();
                userItem.setQuantity(newQty);
                BigDecimal existingUnit = userItem.getUnitPrice() != null
                        ? userItem.getUnitPrice() : unitPrice;
                userItem.setUnitPrice(existingUnit);
                userItem.setTotalPrice(existingUnit.multiply(BigDecimal.valueOf(newQty)));
                userItem.setUpdateUser(userId);
                userItem.setUpdateDate(LocalDateTime.now());
            } else {
                int qty = guestItem.getQuantity() != null && guestItem.getQuantity() > 0
                        ? guestItem.getQuantity() : 1;
                CartItem customerItem = new CartItem();
                customerItem.setEntryUser(guestItem.getEntryUser() != null ? guestItem.getEntryUser() : 0L);
                customerItem.setEntryDate(guestItem.getEntryDate() != null ? guestItem.getEntryDate() : LocalDateTime.now());
                customerItem.setUpdateUser(userId);
                customerItem.setUpdateDate(LocalDateTime.now());
                customerItem.setProduct(guestItem.getProduct());
                customerItem.setQuantity(qty);
                customerItem.setUnitPrice(unitPrice);
                customerItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(qty)));
                customerItem.setCustomer(customer);
                customerItem.setActive(true);
                userItems.add(customerItem);
            }
        }

        cartRepo.saveAll(userItems);
        cartRepo.deleteByCartKey(cartKey);
    }

    @Override
    public CartSummaryModel getCartSummary(BasePageableRequest basePageableRequest) {
        return cartRepo.getCartSummary(CurrentUserContext.getReferenceId(), basePageableRequest.getStringParam1(), basePageableRequest.getStringParam2());
    }

    //----------------------------Helper Functions----------------------------

    private CartItem generateEntity(CartItemDto dto, Boolean isSaved) {
        if (dto.getProduct() == null || dto.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product ID is required to add item to cart");
        }

        Long productId = dto.getProduct().getId();
        Product dbProduct = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        BigDecimal price = BigDecimal.ZERO;
        if (dto.getUnitPrice() != null && dto.getUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
            price = dto.getUnitPrice();
        } else if (dto.getProduct() != null && dto.getProduct().getDiscountPrice() != null) {
            price = dto.getProduct().getDiscountPrice();
        } else if (dbProduct.getDiscountPrice() != null) {
            price = dbProduct.getDiscountPrice();
        } else if (dbProduct.getPrice() != null) {
            price = dbProduct.getPrice();
        }

        Long customerId = CurrentUserContext.getReferenceId();
        String cartKey = dto.getCartKey();

        CartItem entity = null;

        // 1. Try lookup by DTO ID if passed
        if (dto.getId() != null) {
            entity = cartRepo.findById(dto.getId()).orElse(null);
        }

        // 2. Otherwise try lookup by Customer/CartKey + Product ID
        if (entity == null) {
            if (customerId != null) {
                entity = cartRepo.findByCustomerIdAndProductId(customerId, productId).orElse(null);
            } else if (cartKey != null && !cartKey.isBlank()) {
                entity = cartRepo.findByCartKeyAndProductId(cartKey, productId).orElse(null);
            }
        }

        int requestedQty = (dto.getQuantity() != null && dto.getQuantity() > 0) ? dto.getQuantity() : 1;

        if (entity != null) {
            // Updating existing cart item
            entity.setQuantity(requestedQty);
            entity.setUnitPrice(price);
            entity.setTotalPrice(price.multiply(BigDecimal.valueOf(requestedQty)));
            if (dto.getBranchId() != null) {
                entity.setBranchId(dto.getBranchId());
            }
            entity.setUpdateDate(LocalDateTime.now());
            if (customerId != null) {
                entity.setUpdateUser(customerId);
            }
            return entity;
        }

        // Creating brand-new cart item
        entity = new CartItem();
        entity.setProduct(dbProduct);
        entity.setQuantity(requestedQty);
        entity.setUnitPrice(price);
        entity.setTotalPrice(price.multiply(BigDecimal.valueOf(requestedQty)));
        entity.setCartKey(cartKey);
        entity.setBranchId(dto.getBranchId());
        entity.setEntryUser(customerId != null ? customerId : 0L);
        entity.setEntryDate(LocalDateTime.now());
        entity.setActive(true);

        if (customerId != null) {
            Customer customer = customerRepo.findById(customerId).orElse(null);
            entity.setCustomer(customer);
        } else {
            entity.setCustomer(null);
        }

        return entity;
    }

    private List<CartItemDto> convertEntityListToDtoList(Stream<CartItem> entityList) {
        return entityList.map(this::generateDto).collect(Collectors.toList());
    }

    public CartItemDto generateDto(CartItem entity) {
        return modelMapper.map(entity, CartItemDto.class);
    }
}
