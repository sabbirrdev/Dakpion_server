package com.company.efood.delivery.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.OrderDto;
import com.company.efood.sys.services.OrderService;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.user.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "delivery")
@AllArgsConstructor
public class DeliveryController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping("/assign")
    public BaseResponse assignDelivery(@RequestBody OrderDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(orderService.assignDeliveryPartner(body, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/my-orders")
    public BaseResponse getMyOrders(@Valid @RequestBody BasePageableRequest body, HttpServletRequest request) {
        try {
            Long riderId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(orderService.getRiderOrders(riderId, body), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/available-orders")
    public BaseResponse getAvailableOrders(@Valid @RequestBody BasePageableRequest body) {
        try {
            return baseUtils.generateSuccessResponse(orderService.getAssignedOrders(body), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/order/{orderId}/accept")
    public BaseResponse acceptOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long riderId = authTokenUtils.getUserIdFromRequest(request);
            OrderDto accepted = orderService.riderAcceptOrder(orderId, riderId);
            return baseUtils.generateSuccessResponse(accepted, "Order accepted for delivery", "ডেলিভারির জন্য অর্ডার গ্রহণ করা হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping("/cod/collect/{orderId}")
    public BaseResponse collectCodPayment(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String result = paymentService.markCodCollected(orderId, authTokenUtils.getUserIdFromRequest(request));
            return baseUtils.generateSuccessResponse(result, "Cash collected successfully", "ক্যাশ সংগ্রহ সফল হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    /**
     * Rider updates the order status:
     * ASSIGNED → PICKED_UP → ON_THE_WAY → DELIVERED
     * Body: { "status": "PICKED_UP" }
     */
    @PostMapping("/order/{orderId}/status")
    public BaseResponse updateOrderStatusPost(@PathVariable Long orderId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        return handleOrderStatusUpdate(orderId, body, request);
    }

    private BaseResponse handleOrderStatusUpdate(Long orderId, Map<String, String> body, HttpServletRequest request) {
        try {
            Long riderId = authTokenUtils.getUserIdFromRequest(request);
            String status = body.get("status");
            if (status == null) status = body.get("orderStatus");
            OrderDto updated = orderService.updateOrderStatus(orderId, status, riderId);
            return baseUtils.generateSuccessResponse(updated, "Order status updated", "অর্ডারের অবস্থান আপডেট হয়েছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}

