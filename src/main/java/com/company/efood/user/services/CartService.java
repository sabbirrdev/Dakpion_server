package com.company.efood.user.services;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.user.dto.CartItemDto;
import com.company.efood.user.model.CartSummaryModel;
import org.springframework.data.domain.Page;

public interface CartService {
     CartItemDto addToCart(CartItemDto cartDto);
     CartItemDto addItemToCart(com.company.efood.user.dto.CartItemRequestDTO request, Double headerLat, Double headerLon);
     CartItemDto removeItem(CartItemDto cartDto);
     Page<CartItemDto> getPageableCartItems(BasePageableRequest basePageableRequest);
     CartSummaryModel getCartSummary(BasePageableRequest basePageableRequest);
     void clearCart(String cartKey, Long customerId);
     void mergeGuestCartToUser(String cartKey, Long userId);
}
