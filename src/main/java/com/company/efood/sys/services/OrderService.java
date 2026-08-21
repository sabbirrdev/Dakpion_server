package com.company.efood.sys.services;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.sys.dto.OrderDto;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderDto> getCustomerOrders(BasePageableRequest basePageableRequest);
    Page<OrderDto> getCustomerOrders(Long customerUserId, BasePageableRequest basePageableRequest);
    Page<OrderDto> getBranchOrders(BasePageableRequest basePageableRequest);
    Page<OrderDto> getSellerOrders(Long userId, BasePageableRequest basePageableRequest);
    Long placeOrder(OrderDto orderDto, Long userId);
    OrderDto assignDeliveryPartner(OrderDto orderDto, Long userId);
    OrderDto assignRiderToOrder(Long orderId, Long riderId, Long sellerUserId);
    OrderDto cancelOrder(Long orderId, Long userId);
    OrderDto updateOrderStatus(Long orderId, String status, Long riderId);
    OrderDto updateSellerOrderStatus(Long orderId, String status, Long sellerUserId);
    Page<OrderDto> getRiderOrders(Long riderId, BasePageableRequest basePageableRequest);
    Page<OrderDto> getAssignedOrders(BasePageableRequest basePageableRequest);
    OrderDto riderAcceptOrder(Long orderId, Long riderUserId);
    java.util.Map<String, Object> getOrderTracking(Long orderId, Long currentUserId);
    java.util.List<Long> placeMultiOrder(OrderDto orderDto, Long userId);
}
