package com.company.efood.user.controller;


import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.user.dto.CartItemDto;
import com.company.efood.user.entity.CartItem;
import com.company.efood.user.repository.CartRepo;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;

@AllArgsConstructor
@RestController
@RequestMapping(PRIVET_ENDPOINT+"cart")
public class CartController {
    private final CartService cartService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;
    private final CartRepo cartRepo;
    private final CustomerRepo customerRepo;

    @PostMapping("/add")
    public BaseResponse addToCart(@Valid @RequestBody CartItemDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(cartService.addToCart(body), "Item added to cart", "আইটেম কার্টে যোগ হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/remove")
    public BaseResponse removeFromCart(@Valid @RequestBody CartItemDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(cartService.removeItem(body), "Item removed from cart", "আইটেম কার্ট থেকে মুছে ফেলা হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/my")
    public BaseResponse getMyCart(@Valid @RequestBody BasePageableRequest body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(cartService.getPageableCartItems(body), "Cart loaded", "কার্ট লোড হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping(value = "/merge")
    public BaseResponse mergeCart(@Valid @RequestParam String cartKey, HttpServletRequest request) {
        cartService.mergeGuestCartToUser(cartKey, authTokenUtils.getUserIdFromRequest(request));
        return baseUtils.generateSuccessResponse(null, "Cart merged successfully", "কার্ট একীভূত হয়েছে");
    }

    /** Clears ALL cart items for the currently logged-in customer. Called after a successful order placement. */
    @PostMapping("/clear")
    public BaseResponse clearMyCart(HttpServletRequest request) {
        try {
            Long appUserId = CurrentUserContext.getReferenceId();
            customerRepo.findByAppUserId(appUserId).ifPresent(customer -> {
                cartRepo.deleteByCustomerId(customer.getId());
                System.out.println("🛒 [CART] Cleared cart for customerId=" + customer.getId());
            });
            return baseUtils.generateSuccessResponse(null, "Cart cleared", "কার্ট পরিষ্কার করা হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
