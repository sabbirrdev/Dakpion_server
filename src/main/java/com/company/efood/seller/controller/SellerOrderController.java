package com.company.efood.seller.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.services.OrderService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(SELLER_END_POINT + "order")
@AllArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping("/my")
    public BaseResponse getMyOrders(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(orderService.getSellerOrders(userId, basePageableRequest), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{orderId}/status")
    public BaseResponse updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            String status = body.get("status");
            if (status == null || status.isBlank()) {
                return BaseResponse.builder()
                        .status(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Status is required")
                        .build();
            }
            return baseUtils.generateSuccessResponse(
                    orderService.updateSellerOrderStatus(orderId, status, userId),
                    "Order status updated",
                    "অর্ডার স্ট্যাটাস আপডেট হয়েছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{orderId}/approve")
    public BaseResponse approveOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(
                    orderService.updateSellerOrderStatus(orderId, "ACCEPTED", userId),
                    "Order accepted",
                    "অর্ডার গ্রহণ করা হয়েছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{orderId}/prepare")
    public BaseResponse prepareOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(
                    orderService.updateSellerOrderStatus(orderId, "PREPARING", userId),
                    "Order is now preparing",
                    "অর্ডার প্রস্তুত করা হচ্ছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    /**
     * Seller marks order as ready for rider pickup.
     * Correct transition: PREPARING → ASSIGNED (NOT PICKED_UP).
     * ASSIGNED means the order is ready at shop and available for rider to collect.
     */
    @PostMapping("/{orderId}/ready")
    public BaseResponse readyOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(
                    orderService.updateSellerOrderStatus(orderId, "ASSIGNED", userId),
                    "Order ready — riders notified for pickup",
                    "অর্ডার প্রস্তুত — রাইডার পিকআপের জন্য নোটিফাই করা হয়েছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    /**
     * Seller manually assigns a specific rider to an order.
     * Body: { "riderId": 123 }
     */
    @PostMapping("/{orderId}/assign-rider")
    public BaseResponse assignRider(@PathVariable Long orderId, @RequestBody Map<String, Long> body, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            Long riderId = body.get("riderId");
            if (riderId == null) {
                return BaseResponse.builder()
                        .status(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("riderId is required")
                        .build();
            }
            return baseUtils.generateSuccessResponse(
                    orderService.assignRiderToOrder(orderId, riderId, userId),
                    "Rider assigned to order",
                    "রাইডার অ্যাসাইন করা হয়েছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/{orderId}/reject")
    public BaseResponse rejectOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(
                    orderService.updateSellerOrderStatus(orderId, "REJECTED", userId),
                    "Order rejected",
                    "অর্ডার প্রত্যাখ্যাত হয়েছে"
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
