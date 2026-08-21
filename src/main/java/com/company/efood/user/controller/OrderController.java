package com.company.efood.user.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.OrderDto;
import com.company.efood.sys.services.OrderService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "order")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping(value = "/my")
    public BaseResponse getMyOrders (@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
       try{
           return  baseUtils.generateSuccessResponse(orderService.getCustomerOrders(authTokenUtils.getUserIdFromRequest(request), basePageableRequest), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
       } catch (Exception ex){
           return baseUtils.generateErrorResponse(ex);
       }
    }

    @PostMapping("/place")
    public BaseResponse placeOrder(@Valid @RequestBody  OrderDto orderDto, HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(orderService.placeOrder(orderDto, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return   baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/place-multi")
    public BaseResponse placeMultiOrder(@Valid @RequestBody OrderDto orderDto, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(orderService.placeMultiOrder(orderDto, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/cancel/{orderId}")
    public BaseResponse cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(orderService.cancelOrder(orderId, authTokenUtils.getUserIdFromRequest(request)), "Order cancelled successfully", "অর্ডার বাতিল করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/{orderId}/tracking")
    public BaseResponse getOrderTracking(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(orderService.getOrderTracking(orderId, userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
