package com.company.efood.user.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.user.dto.CartItemDto;
import com.company.efood.user.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import static com.company.efood.base.BaseConstants.*;

@AllArgsConstructor
@RestController
@RequestMapping(PUBLIC_ENDPOINT+"cart")
public class PublicCartController {

    private final CartService cartService;
    private final BaseUtils baseUtils;

    @PostMapping("/add")
    public BaseResponse addToCart(@Valid @RequestBody CartItemDto CartItemDto, HttpServletRequest request) {
       try{
           return  baseUtils.generateSuccessResponse(cartService.addToCart(CartItemDto),SAVE_MESSAGE,SAVE_MESSAGE_BN);
       } catch (Exception ex) {
         return  baseUtils.generateErrorResponse(ex);
       }
    }

    @DeleteMapping("/remove")
    public BaseResponse removeFromCart(@RequestBody CartItemDto cartItemDto) {
        try{
            return  baseUtils.generateSuccessResponse(cartService.removeItem(cartItemDto), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception ex) {
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PutMapping
    public BaseResponse getCart(@RequestBody BasePageableRequest basePageableRequest,HttpServletRequest httpServletRequest) {
        try{
            return  baseUtils.generateSuccessResponse(cartService.getPageableCartItems(basePageableRequest), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @DeleteMapping("/clear")
    public BaseResponse clearCart(@RequestParam(required = false) String cartKey, @RequestParam(required = false) Long customerId) {
        try{
            cartService.clearCart(cartKey, customerId);
            return  baseUtils.generateSuccessResponse(null, DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception ex) {
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/summary")
    public BaseResponse getCartSummary(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(cartService.getCartSummary(basePageableRequest),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return  baseUtils.generateErrorResponse(ex);
        }
    }
}

