package com.company.efood.sys.services.serviceimpl;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.raider.entity.Raider;
import com.company.efood.raider.repository.RaiderRepo;
import com.company.efood.seller.repository.BranchRepo;
import com.company.efood.sys.dto.OrderDto;
import com.company.efood.sys.dto.OrderItemDto;
import com.company.efood.sys.dto.PaymentDto;
import com.company.efood.sys.dto.ProductDto;
import com.company.efood.sys.dto.AddressDto;
import com.company.efood.sys.entity.*;
import com.company.efood.sys.repository.*;
import com.company.efood.sys.services.OrderService;
import com.company.efood.sys.utils.AddressType;
import com.company.efood.sys.utils.OrderStatus;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.repository.CartRepo;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.repository.OrderItemRepo;
import com.company.efood.user.services.PaymentService;
import com.company.efood.sys.utils.PaymentStatus;
import com.company.efood.websocket.service.LiveNotificationService;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepository;
    private final CustomerRepo customerRepo;
    private final BaseUtils baseUtils;
    private final OrderRepo orderRepo;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;
    private final BranchRepo branchRepo;
    private final AddressRepo addressRepo;
    private final RaiderRepo raiderRepo;
    private final ProductRepo productRepo;
    private final PaymentRepo paymentRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartRepo cartRepo;
    private final LiveNotificationService liveNotificationService;
    private final com.company.efood.notification.service.FcmNotificationService fcmNotificationService;
    private final org.springframework.context.ApplicationEventPublisher applicationEventPublisher;
    private final com.company.efood.user.services.LoyaltyService loyaltyService;
    private final com.company.efood.raider.repository.RaiderLiveLocationRepo raiderLiveLocationRepo;


    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getCustomerOrders(BasePageableRequest basePageableRequest) {
        Long customerId = CurrentUserContext.getReferenceId();
        return getCustomerOrders(customerId, basePageableRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getCustomerOrders(Long customerUserId, BasePageableRequest basePageableRequest) {
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());
        if (customerUserId == null) {
            customerUserId = CurrentUserContext.getReferenceId();
        }
        if (customerUserId == null) {
            return Page.empty(pageRequest);
        }

        Page<Order> orderPage = orderRepository.findByCustomerAppUserId(customerUserId, pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(orderPage.stream()), pageRequest, orderPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getBranchOrders(BasePageableRequest basePageableRequest) {
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());
        Page<Order> orderPage = orderRepository.findByBranchId(basePageableRequest.getIntParam1(), pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(orderPage.stream()), pageRequest, orderPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getSellerOrders(Long userId, BasePageableRequest basePageableRequest) {
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());

        Page<Order> orderPage;
        if (basePageableRequest.getIntParam1() != null) {
            orderPage = orderRepository.findByBranchId(basePageableRequest.getIntParam1(), pageRequest);
        } else {
            orderPage = orderRepository.findBySellerAppUserId(userId, pageRequest);
        }

        return new PageImpl<>(convertEntityListToDtoList(orderPage.stream()), pageRequest, orderPage.getTotalElements());
    }

    @Transactional
    @Override
    public OrderDto updateSellerOrderStatus(Long orderId, String status, Long sellerUserId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        OrderStatus prevStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdateUser(sellerUserId);
        Order updatedOrder = orderRepo.save(order);

        applicationEventPublisher.publishEvent(new com.company.efood.sys.event.OrderStatusChangedEvent(
                this, updatedOrder.getId(), prevStatus, newStatus, sellerUserId));

        String statusMsg = switch (newStatus) {
            case ACCEPTED -> "Order accepted by restaurant/shop";
            case PREPARING -> "Order is being prepared";
            case PICKED_UP, COMPLETED -> "Order is ready for pickup";
            case REJECTED, CANCELLED -> "Order was rejected by restaurant/shop";
            default -> "Order status updated";
        };

        liveNotificationService.sendOrderStatusUpdate(updatedOrder.getId(), newStatus.name(), statusMsg);

        try {
            if (newStatus == OrderStatus.PREPARING) {
                String shopName = (order.getBranch() != null && order.getBranch().getShop() != null) ? order.getBranch().getShop().getShopName() : "Shop";
                String address = (order.getDeliveryAddress() != null) ? order.getDeliveryAddress().getAddress() : "";
                fcmNotificationService.notifyRidersOrderPreparing(updatedOrder.getId(), shopName, address);
            }
            if (order.getCustomer() != null && order.getCustomer().getAppUser() != null) {
                fcmNotificationService.notifyCustomerOrderStatus(order.getCustomer().getAppUser().getId(), updatedOrder.getId(), newStatus.name(), statusMsg);
            }
        } catch (Exception e) {
            System.err.println("Failed to dispatch push notification: " + e.getMessage());
        }

        return generateDto(updatedOrder);
    }

    @Transactional
    @Override
    public Long placeOrder(OrderDto orderDto, Long userId) {
        System.out.println("==========================================================");
        System.out.println("🛒 [ORDER SERVICE] placeOrder() called by userId=" + userId);
        System.out.println("📦 [ORDER SERVICE] OrderDto deliveryAddress=" + orderDto.getDeliveryAddress());
        System.out.println("📦 [ORDER SERVICE] OrderDto branchId=" + orderDto.getBranchId());
        System.out.println("📦 [ORDER SERVICE] OrderDto items count=" + (orderDto.getItems() != null ? orderDto.getItems().size() : 0));

        Long customerId = CurrentUserContext.getReferenceId();
        System.out.println("👤 [ORDER SERVICE] CurrentUserContext.getReferenceId() = " + customerId);
        if (customerId == null) {
            System.out.println("❌ [ORDER SERVICE] Customer context is null — user not authenticated or referenceId missing from JWT");
            throw new IllegalArgumentException("Customer context not found");
        }

        Address address = null;
        if (orderDto.getDeliveryAddress() != null) {
            System.out.println("📍 [ORDER SERVICE] DeliveryAddress received: id=" + orderDto.getDeliveryAddress().getId()
                    + " | type=" + orderDto.getDeliveryAddress().getAddressType()
                    + " | address=" + orderDto.getDeliveryAddress().getAddress()
                    + " | district=" + orderDto.getDeliveryAddress().getDistrict()
                    + " | lat=" + orderDto.getDeliveryAddress().getLat()
                    + " | lon=" + orderDto.getDeliveryAddress().getLon());

            if (orderDto.getDeliveryAddress().getId() != null) {
                System.out.println("🔍 [ORDER SERVICE] Looking up existing address by id=" + orderDto.getDeliveryAddress().getId());
                address = addressRepo.findById(orderDto.getDeliveryAddress().getId()).orElse(null);
                System.out.println("🔍 [ORDER SERVICE] Existing address lookup result: " + (address != null ? "FOUND id=" + address.getId() : "NOT FOUND"));
            }

            if (address == null) {
                System.out.println("➕ [ORDER SERVICE] Creating new address entity...");
                Address newAddress = new Address();
                newAddress.setAddress(orderDto.getDeliveryAddress().getAddress() != null
                        ? orderDto.getDeliveryAddress().getAddress() : "Default Address");
                newAddress.setDistrict(orderDto.getDeliveryAddress().getDistrict() != null
                        ? orderDto.getDeliveryAddress().getDistrict() : "N/A");
                newAddress.setPoliceStation(orderDto.getDeliveryAddress().getPoliceStation() != null
                        ? orderDto.getDeliveryAddress().getPoliceStation() : "N/A");
                newAddress.setAddressType(orderDto.getDeliveryAddress().getAddressType() != null
                        ? orderDto.getDeliveryAddress().getAddressType() : AddressType.HOME_ADDRESS);
                // These comparisons are now valid because AddressDto.lat/lon are Double (wrapper, not primitive)
                if (orderDto.getDeliveryAddress().getLat() != null) {
                    newAddress.setLat(orderDto.getDeliveryAddress().getLat());
                    System.out.println("📍 [ORDER SERVICE] lat set to: " + orderDto.getDeliveryAddress().getLat());
                } else {
                    System.out.println("⚠️ [ORDER SERVICE] lat is null — defaulting to 0.0");
                }
                if (orderDto.getDeliveryAddress().getLon() != null) {
                    newAddress.setLon(orderDto.getDeliveryAddress().getLon());
                    System.out.println("📍 [ORDER SERVICE] lon set to: " + orderDto.getDeliveryAddress().getLon());
                } else {
                    System.out.println("⚠️ [ORDER SERVICE] lon is null — defaulting to 0.0");
                }
                newAddress.setEntryUser(userId);
                baseUtils.setEntryUserInfo(newAddress);
                address = addressRepo.save(newAddress);
                System.out.println("✅ [ORDER SERVICE] New address saved with id=" + address.getId());
            }
        } else {
            System.out.println("❌ [ORDER SERVICE] deliveryAddress is NULL in OrderDto — delivery address is required!");
            throw new IllegalArgumentException("Delivery address is required");
        }

        if (orderDto.getBranchId() == null) {
            System.out.println("❌ [ORDER SERVICE] branchId is NULL");
            throw new IllegalArgumentException("Branch ID is required");
        }

        if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            System.out.println("❌ [ORDER SERVICE] items list is empty/null");
            throw new IllegalArgumentException("At least one order item is required for checkout");
        }

        // BUG FIX: `CurrentUserContext.getReferenceId()` is the Customer table's
        // own primary key for a CUSTOMER-role user (see CurrentUserInfo /
        // CartServiceImpl's identical, correct usage), NOT the linked
        // AppUser's id. This used to call `customerRepo.findByAppUserId(referenceId)`
        // — comparing a Customer.id against an AppUser.id — which only matched
        // by coincidence and otherwise silently fell back to
        // `customerRepo.findAll().stream().findFirst()`, attaching the order
        // (and clearing the cart of) a completely different, effectively
        // random customer. That's how an order could be confirmed in the DB
        // while "my orders" for the customer who actually placed it stayed
        // empty, and their cart was left un-cleared. Look the customer up by
        // their own id and fail loudly instead of guessing.
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        System.out.println("👤 [ORDER SERVICE] Customer found: id=" + customer.getId());

        // Check if items span multiple branches — use item.branchId first, then product.branchId
        java.util.Set<Long> uniqueBranches = new java.util.HashSet<>();
        if (orderDto.getItems() != null) {
            for (OrderItemDto item : orderDto.getItems()) {
                Long itemBranchId = item.getBranchId();
                if (itemBranchId == null && item.getProduct() != null) {
                    itemBranchId = item.getProduct().getBranchId();
                }
                if (itemBranchId != null) {
                    uniqueBranches.add(itemBranchId);
                }
            }
        }

        // If items span multiple branches, delegate to atomic multi-order placement
        if (uniqueBranches.size() > 1) {
            System.out.println("🔀 [ORDER SERVICE] Multi-branch cart detected (" + uniqueBranches.size() + " branches). Placing multi-vendor order atomically...");
            java.util.List<Long> multiOrderIds = placeMultiOrder(orderDto, userId);
            return multiOrderIds.get(0);
        }

        Branch branch = branchRepo.findById(orderDto.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + orderDto.getBranchId()));
        System.out.println("🏬 [ORDER SERVICE] Branch found: id=" + branch.getId());

        List<OrderItem> orderItems = orderDto.getItems().stream().map(item -> mapOrderItem(item, userId)).collect(Collectors.toList());
        BigDecimal itemTotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("🧾 [ORDER SERVICE] Order items mapped: count=" + orderItems.size() + " | itemTotal=" + itemTotal);

        BigDecimal deliveryFee = orderDto.getDeliveryFee() != null && orderDto.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0
                ? orderDto.getDeliveryFee()
                : BigDecimal.valueOf(10.0);
        BigDecimal discount = orderDto.getDiscount() != null ? orderDto.getDiscount() : BigDecimal.ZERO;
        BigDecimal tax = orderDto.getTax() != null ? orderDto.getTax() : BigDecimal.ZERO;

        BigDecimal preLoyaltyTotal = itemTotal.add(deliveryFee).subtract(discount).add(tax);
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        if (orderDto.getLoyaltyPointsToRedeem() != null && orderDto.getLoyaltyPointsToRedeem() > 0) {
            loyaltyDiscount = loyaltyService.redeemPointsForOrder(
                    customer.getId(),
                    null,
                    orderDto.getLoyaltyPointsToRedeem(),
                    preLoyaltyTotal
            );
            discount = discount.add(loyaltyDiscount);
            System.out.println("🎁 [ORDER SERVICE] Applied loyalty discount: " + loyaltyDiscount + " using " + orderDto.getLoyaltyPointsToRedeem() + " points");
        }

        BigDecimal grandTotal = itemTotal.add(deliveryFee).subtract(discount).add(tax);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setBranch(branch);
        order.setDeliveryAddress(address);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(orderDto.getTotalAmount() != null && orderDto.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 ? orderDto.getTotalAmount() : grandTotal);
        order.setDiscount(discount);
        order.setTax(tax);
        order.setDeliveryFee(deliveryFee);
        order.setEntryUser(userId);
        order.setActive(true);
        baseUtils.setEntryUserInfo(order);

        System.out.println("💾 [ORDER SERVICE] Saving order entity...");
        Order savedOrder = orderRepo.save(order);
        System.out.println("✅ [ORDER SERVICE] Order saved with id=" + savedOrder.getId());

        orderItems.forEach(item -> item.setOrder(savedOrder));
        savedOrder.setOrderItemList(orderItems);
        orderItemRepo.saveAll(orderItems);
        System.out.println("✅ [ORDER SERVICE] Order items saved.");

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setOrderId(savedOrder.getId());
        paymentDto.setPaymentMethod(orderDto.getPaymentMethod());
        System.out.println("💳 [ORDER SERVICE] Initiating payment: method=" + orderDto.getPaymentMethod());
        paymentService.initiatePayment(paymentDto, userId);

        if (customer != null) {
            try {
                cartRepo.deleteByCustomerId(customer.getId());
                System.out.println("🛒 [ORDER SERVICE] Cleared cart items from DB for customerId=" + customer.getId());
            } catch (Exception e) {
                System.out.println("⚠️ [ORDER SERVICE] Failed to clear cart items: " + e.getMessage());
            }
        }

        liveNotificationService.sendOrderStatusUpdate(savedOrder.getId(), OrderStatus.PLACED.name(), "Order placed successfully");
        System.out.println("🎉 [ORDER SERVICE] Order placement COMPLETE. Order ID=" + savedOrder.getId());
        System.out.println("==========================================================");
        return savedOrder.getId();
    }

    @Transactional
    @Override
    public java.util.List<Long> placeMultiOrder(OrderDto orderDto, Long userId) {
        if (orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required for checkout");
        }

        Long resolvedCustomerId = CurrentUserContext.getReferenceId();
        if (resolvedCustomerId == null) resolvedCustomerId = userId;
        final Long customerId = resolvedCustomerId;
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Address address = null;
        if (orderDto.getDeliveryAddress() != null) {
            if (orderDto.getDeliveryAddress().getId() != null) {
                address = addressRepo.findById(orderDto.getDeliveryAddress().getId()).orElse(null);
            }
            if (address == null) {
                Address newAddress = new Address();
                AddressDto addrDto = orderDto.getDeliveryAddress();
                newAddress.setAddress(addrDto.getAddress() != null ? addrDto.getAddress() : "");
                newAddress.setDistrict(addrDto.getDistrict() != null ? addrDto.getDistrict() : "N/A");
                newAddress.setPoliceStation(addrDto.getPoliceStation() != null ? addrDto.getPoliceStation() : "N/A");
                newAddress.setAddressType(addrDto.getAddressType() != null ? addrDto.getAddressType() : AddressType.HOME_ADDRESS);
                if (addrDto.getLat() != null) newAddress.setLat(addrDto.getLat());
                if (addrDto.getLon() != null) newAddress.setLon(addrDto.getLon());
                newAddress.setEntryUser(userId);
                baseUtils.setEntryUserInfo(newAddress);
                address = addressRepo.save(newAddress);
            }
        }
        if (address == null) {
            throw new IllegalArgumentException("Delivery address is required");
        }

        // Group items by branchId — use OrderItemDto.branchId first, then product.branchId
        java.util.Map<Long, java.util.List<OrderItemDto>> itemsByBranch = new java.util.LinkedHashMap<>();
        for (OrderItemDto item : orderDto.getItems()) {
            Long branchId = item.getBranchId();
            if (branchId == null && item.getProduct() != null) {
                branchId = item.getProduct().getBranchId();
            }
            if (branchId == null) {
                branchId = orderDto.getBranchId();
            }
            if (branchId == null) {
                throw new IllegalArgumentException("Cannot determine branch for item: product=" +
                        (item.getProduct() != null ? item.getProduct().getId() : "null"));
            }
            itemsByBranch.computeIfAbsent(branchId, k -> new java.util.ArrayList<>()).add(item);
        }

        int branchCount = itemsByBranch.size();
        BigDecimal totalLoyaltyDiscount = BigDecimal.ZERO;
        if (orderDto.getLoyaltyPointsToRedeem() != null && orderDto.getLoyaltyPointsToRedeem() > 0) {
            BigDecimal estimatedTotal = orderDto.getTotalAmount() != null ? orderDto.getTotalAmount() : BigDecimal.valueOf(100);
            totalLoyaltyDiscount = loyaltyService.redeemPointsForOrder(
                    customer.getId(),
                    null,
                    orderDto.getLoyaltyPointsToRedeem(),
                    estimatedTotal
            );
        }

        BigDecimal discountPerBranch = branchCount > 0
                ? totalLoyaltyDiscount.divide(BigDecimal.valueOf(branchCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        java.util.List<Long> createdOrderIds = new java.util.ArrayList<>();

        for (java.util.Map.Entry<Long, java.util.List<OrderItemDto>> entry : itemsByBranch.entrySet()) {
            Long branchId = entry.getKey();
            java.util.List<OrderItemDto> branchItems = entry.getValue();

            Branch branch = branchRepo.findById(branchId)
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + branchId));

            List<OrderItem> orderItems = branchItems.stream().map(item -> mapOrderItem(item, userId)).collect(Collectors.toList());
            BigDecimal itemTotal = orderItems.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal deliveryFee = orderDto.getDeliveryFee() != null && orderDto.getDeliveryFee().compareTo(BigDecimal.ZERO) > 0
                    ? orderDto.getDeliveryFee()
                    : BigDecimal.valueOf(10.0);
            BigDecimal discount = discountPerBranch;
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal grandTotal = itemTotal.add(deliveryFee).subtract(discount).add(tax);
            if (grandTotal.compareTo(BigDecimal.ZERO) < 0) grandTotal = BigDecimal.ZERO;

            Order branchOrder = new Order();
            branchOrder.setCustomer(customer);
            branchOrder.setBranch(branch);
            branchOrder.setDeliveryAddress(address);
            branchOrder.setStatus(OrderStatus.PLACED);
            branchOrder.setTotalAmount(grandTotal);
            branchOrder.setDiscount(discount);
            branchOrder.setTax(tax);
            branchOrder.setDeliveryFee(deliveryFee);
            branchOrder.setEntryUser(userId);
            branchOrder.setActive(true);
            baseUtils.setEntryUserInfo(branchOrder);

            Order savedBranchOrder = orderRepo.save(branchOrder);
            orderItems.forEach(item -> item.setOrder(savedBranchOrder));
            savedBranchOrder.setOrderItemList(orderItems);
            orderItemRepo.saveAll(orderItems);

            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setOrderId(savedBranchOrder.getId());
            paymentDto.setPaymentMethod(orderDto.getPaymentMethod());
            paymentService.initiatePayment(paymentDto, userId);

            liveNotificationService.sendOrderStatusUpdate(savedBranchOrder.getId(), OrderStatus.PLACED.name(), "Order placed for branch " + branch.getName());
            createdOrderIds.add(savedBranchOrder.getId());
        }

        // Clear cart once all branch orders succeed
        try {
            cartRepo.deleteByCustomerId(customer.getId());
        } catch (Exception e) {
            System.out.println("⚠️ [ORDER SERVICE] Failed to clear cart: " + e.getMessage());
        }

        return createdOrderIds;
    }

    @Transactional
    @Override
    public OrderDto assignDeliveryPartner(OrderDto orderDto, Long userId) {
        if (orderDto == null || orderDto.getId() == null) {
            throw new IllegalArgumentException("Order ID is required to assign a delivery partner");
        }

        Order order = orderRepo.findById(orderDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderDto.getId()));

        Raider raider = resolveRaider(orderDto);
        if (raider == null) {
            throw new IllegalArgumentException("No available delivery partner found for the requested vehicle type");
        }

        order.setRaider(raider);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setUpdateUser(userId);
        baseUtils.setUpdateUserInfo(order, orderRepo.findById(order.getId()).orElse(order));
        Order updatedOrder = orderRepo.save(order);
        liveNotificationService.sendRiderAssignment(updatedOrder.getId(), raider.getId(), raider.getVehicleType());
        liveNotificationService.sendOrderStatusUpdate(updatedOrder.getId(), OrderStatus.ASSIGNED.name(), "Rider assigned to the order");
        return generateDto(updatedOrder);
    }

    @Transactional
    @Override
    public OrderDto cancelOrder(Long orderId, Long userId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required for cancellation");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("The order cannot be cancelled in its current state");
        }

        if (order.getOrderItemList() != null) {
            for (OrderItem item : order.getOrderItemList()) {
                Product product = item.getProduct();
                if (product == null) {
                    continue;
                }
                Integer currentQty = product.getQty() != null ? product.getQty() : 0;
                product.setQty(currentQty + item.getQuantity());
                productRepo.save(product);
            }
        }

        Payment payment = paymentRepo.findByOrderId(orderId).orElse(null);
        if (payment != null) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setEntryUser(userId);
            paymentRepo.save(payment);
        }

        OrderStatus prevCancelStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdateUser(userId);
        baseUtils.setUpdateUserInfo(order, orderRepo.findById(order.getId()).orElse(order));
        Order updatedOrder = orderRepo.save(order);
        applicationEventPublisher.publishEvent(new com.company.efood.sys.event.OrderStatusChangedEvent(
                this, updatedOrder.getId(), prevCancelStatus, OrderStatus.CANCELLED, userId));
        liveNotificationService.sendOrderStatusUpdate(updatedOrder.getId(), OrderStatus.CANCELLED.name(), "Order cancelled successfully");
        return generateDto(updatedOrder);
    }

    @Transactional
    @Override
    public OrderDto updateOrderStatus(Long orderId, String status, Long riderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Security / Assignment resolution
        Raider currentRider = raiderRepo.findByAppUserId(riderId)
                .orElseGet(() -> raiderRepo.findById(riderId).orElse(null));

        if (order.getRaider() == null) {
            // No rider assigned yet — auto-assign this rider
            if (currentRider != null) {
                order.setRaider(currentRider);
            }
        } else {
            // Check if this rider is the assigned one (compare by Raider entity ID and by AppUser ID)
            Long orderRaiderId = order.getRaider().getId();
            Long orderRaiderAppUserId = (order.getRaider().getAppUser() != null)
                    ? order.getRaider().getAppUser().getId() : null;

            boolean isAssigned = false;
            if (currentRider != null && Objects.equals(orderRaiderId, currentRider.getId())) {
                isAssigned = true;
            } else if (Objects.equals(orderRaiderAppUserId, riderId)) {
                isAssigned = true;
            } else if (Objects.equals(orderRaiderId, riderId)) {
                // fallback: maybe riderId == Raider entity ID (old behavior)
                isAssigned = true;
            }

            if (!isAssigned) {
                throw new IllegalArgumentException("You are not assigned to this order (orderId=" + orderId
                        + ", assignedRaiderId=" + orderRaiderId
                        + ", assignedRaiderUserId=" + orderRaiderAppUserId
                        + ", requestRiderId=" + riderId + ")");
            }
        }

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        // Allow only valid rider transitions
        OrderStatus currentStatus = order.getStatus();
        boolean validTransition =
                (currentStatus == OrderStatus.ASSIGNED && newStatus == OrderStatus.PICKED_UP) ||
                (currentStatus == OrderStatus.PICKED_UP && newStatus == OrderStatus.ON_THE_WAY) ||
                (currentStatus == OrderStatus.ON_THE_WAY && newStatus == OrderStatus.DELIVERED) ||
                (currentStatus == OrderStatus.DELIVERED && newStatus == OrderStatus.COMPLETED);

        if (!validTransition) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + newStatus
                    + ". Valid: ASSIGNED→PICKED_UP, PICKED_UP→ON_THE_WAY, ON_THE_WAY→DELIVERED, DELIVERED→COMPLETED");
        }

        OrderStatus prevStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdateUser(riderId);
        Order updatedOrder = orderRepo.save(order);

        applicationEventPublisher.publishEvent(new com.company.efood.sys.event.OrderStatusChangedEvent(
                this, updatedOrder.getId(), prevStatus, newStatus, riderId));

        String statusMsg = switch (newStatus) {
            case PICKED_UP -> "Order picked up by rider";
            case ON_THE_WAY -> "Order is on the way";
            case DELIVERED -> "Order delivered successfully";
            case COMPLETED -> "Order completed";
            default -> "Order status updated";
        };

        liveNotificationService.sendOrderStatusUpdate(updatedOrder.getId(), newStatus.name(), statusMsg);

        try {
            if (order.getCustomer() != null && order.getCustomer().getAppUser() != null) {
                fcmNotificationService.notifyCustomerOrderStatus(order.getCustomer().getAppUser().getId(), updatedOrder.getId(), newStatus.name(), statusMsg);
            }
        } catch (Exception e) {
            System.err.println("Failed to dispatch push notification: " + e.getMessage());
        }

        return generateDto(updatedOrder);
    }


    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getRiderOrders(Long riderId, BasePageableRequest basePageableRequest) {
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());
        // Find the Raider entity by userId first
        Raider raider = raiderRepo.findByAppUserId(riderId).orElse(null);
        if (raider == null) {
            return Page.empty(pageRequest);
        }
        Page<Order> orderPage = orderRepo.findByRaiderId(raider.getId(), pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(orderPage.stream()), pageRequest, orderPage.getTotalElements());
    }



    @Transactional
    @Override
    public OrderDto assignRiderToOrder(Long orderId, Long riderId, Long sellerUserId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        Raider raider = raiderRepo.findById(riderId)
                .orElseThrow(() -> new IllegalArgumentException("Rider not found: " + riderId));
        order.setRaider(raider);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setUpdateUser(sellerUserId);
        Order saved = orderRepo.save(order);
        liveNotificationService.sendRiderAssignment(saved.getId(), raider.getId(), raider.getVehicleType());
        liveNotificationService.sendOrderStatusUpdate(saved.getId(), OrderStatus.ASSIGNED.name(), "Rider assigned — order ready for pickup");

        try {
            if (raider.getAppUser() != null) {
                String shopName = (order.getBranch() != null && order.getBranch().getShop() != null) ? order.getBranch().getShop().getShopName() : "Shop";
                fcmNotificationService.notifyRiderOrderAssigned(raider.getAppUser().getId(), saved.getId(), shopName);
            }
            if (order.getCustomer() != null && order.getCustomer().getAppUser() != null) {
                fcmNotificationService.notifyCustomerOrderStatus(order.getCustomer().getAppUser().getId(), saved.getId(), "ASSIGNED", "Rider assigned to your order");
            }
        } catch (Exception e) {
            System.err.println("Failed to dispatch push notification: " + e.getMessage());
        }

        return generateDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderDto> getAssignedOrders(BasePageableRequest basePageableRequest) {
        PageRequest pageRequest = baseUtils.getPageRequest(basePageableRequest.getPage(), basePageableRequest.getSize());
        // Only return orders that are ASSIGNED/Ready but NOT YET CLAIMED by any rider
        Page<Order> page = orderRepo.findByStatusAndRaiderIsNull(OrderStatus.ASSIGNED, pageRequest);
        return new PageImpl<>(convertEntityListToDtoList(page.stream()), pageRequest, page.getTotalElements());
    }

    @Transactional
    @Override
    public OrderDto riderAcceptOrder(Long orderId, Long riderUserId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        Raider raider = raiderRepo.findByAppUserId(riderUserId)
                .orElseGet(() -> raiderRepo.findById(riderUserId).orElse(null));
        if (raider == null) {
            throw new IllegalArgumentException("Rider profile not found for user: " + riderUserId);
        }

        order.setRaider(raider);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setUpdateUser(riderUserId);
        Order saved = orderRepo.save(order);
        liveNotificationService.sendRiderAssignment(saved.getId(), raider.getId(), raider.getVehicleType());
        liveNotificationService.sendOrderStatusUpdate(saved.getId(), OrderStatus.ASSIGNED.name(), "Rider accepted delivery");
        return generateDto(saved);
    }

    private OrderItem mapOrderItem(OrderItemDto itemDto, Long userId) {
        if (itemDto == null || itemDto.getProduct() == null || itemDto.getProduct().getId() == null) {
            throw new IllegalArgumentException("Each order item must include a valid product reference");
        }

        if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Each order item quantity must be greater than zero");
        }

        Product product = productRepo.findById(itemDto.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemDto.getProduct().getId()));

        if (product.getQty() == null || product.getQty() < itemDto.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName());
        }

        BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));

        product.setQty(product.getQty() - itemDto.getQuantity());
        productRepo.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setUnitPrice(unitPrice);
        orderItem.setTotalPrice(totalPrice);
        orderItem.setEntryUser(userId);
        orderItem.setActive(true);
        baseUtils.setEntryUserInfo(orderItem);
        return orderItem;
    }

    private Raider resolveRaider(OrderDto orderDto) {
        if (orderDto.getRaiderId() != null) {
            return raiderRepo.findById(orderDto.getRaiderId())
                    .orElseThrow(() -> new IllegalArgumentException("Raider not found: " + orderDto.getRaiderId()));
        }

        if (orderDto.getVehicleType() != null && !orderDto.getVehicleType().isBlank()) {
            return raiderRepo.findFirstByIsAvailableTrueAndVehicleTypeIgnoreCase(orderDto.getVehicleType().trim())
                    .orElse(null);
        }

        return raiderRepo.findByIsAvailableTrue().stream().findFirst().orElse(null);
    }


    // Helper method to convert CartItem to OrderItem
    private List<OrderDto> convertEntityListToDtoList(Stream<Order> entityList) {
        return entityList.map(this::generateDto).collect(Collectors.toList());
    }


    public OrderDto generateDto(Order entity) {
        if (entity == null) return null;
        OrderDto dto = modelMapper.map(entity, OrderDto.class);
        
        List<OrderItem> orderItems = entity.getOrderItemList();
        if ((orderItems == null || orderItems.isEmpty()) && entity.getId() != null) {
            try {
                orderItems = orderItemRepo.findByOrderId(entity.getId());
            } catch (Exception ignored) {}
        }
        
        if (orderItems != null && !orderItems.isEmpty()) {
            dto.setItems(orderItems.stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setOrderId(entity.getId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setTotalPrice(item.getTotalPrice());
                if (item.getProduct() != null) {
                    ProductDto pDto = new ProductDto();
                    pDto.setId(item.getProduct().getId());
                    pDto.setProductName(item.getProduct().getProductName());
                    pDto.setImgUrl(item.getProduct().getImgUrl());
                    pDto.setPrice(item.getProduct().getPrice());
                    pDto.setDiscountPrice(item.getProduct().getDiscountPrice());
                    if (item.getProduct().getBranch() != null) {
                        pDto.setBranchId(item.getProduct().getBranch().getId());
                    }
                    itemDto.setProduct(pDto);
                }
                return itemDto;
            }).collect(Collectors.toList()));
        }
        if (entity.getDeliveryAddress() != null) {
            AddressDto aDto = modelMapper.map(entity.getDeliveryAddress(), AddressDto.class);
            dto.setDeliveryAddress(aDto);
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            if (entity.getCustomer().getFullName() != null && !entity.getCustomer().getFullName().isBlank()) {
                dto.setCustomerName(entity.getCustomer().getFullName());
            } else if (entity.getCustomer().getAppUser() != null) {
                dto.setCustomerName(entity.getCustomer().getAppUser().getDisplayName());
            }
            if (entity.getCustomer().getPhone() != null && !entity.getCustomer().getPhone().isBlank()) {
                dto.setCustomerPhone(entity.getCustomer().getPhone());
            } else if (entity.getCustomer().getAppUser() != null) {
                dto.setCustomerPhone(entity.getCustomer().getAppUser().getUsername());
            }
        }
        if (entity.getBranch() != null) {
            dto.setBranchId(entity.getBranch().getId());
        }
        if (entity.getDeliveryFee() != null) {
            dto.setDeliveryFee(entity.getDeliveryFee());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public java.util.Map<String, Object> getOrderTracking(Long orderId, Long currentUserId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        Customer customer = order.getCustomer();
        boolean isOwner = customer != null && (
                (customer.getId() != null && customer.getId().equals(currentUserId)) ||
                (customer.getAppUser() != null && customer.getAppUser().getId().equals(currentUserId))
        );
        boolean isAssignedRider = order.getRaider() != null && order.getRaider().getAppUser() != null
                && order.getRaider().getAppUser().getId().equals(currentUserId);

        if (!isOwner && !isAssignedRider) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to track this order");
        }

        java.util.Map<String, Object> tracking = new java.util.HashMap<>();
        tracking.put("orderId", order.getId());
        tracking.put("orderStatus", order.getStatus() != null ? order.getStatus().name() : null);

        if (order.getBranch() != null) {
            tracking.put("branchId", order.getBranch().getId());
            tracking.put("branchName", order.getBranch().getName());
            if (order.getBranch().getAddress() != null) {
                tracking.put("pickupAddress", order.getBranch().getAddress().getAddress());
                tracking.put("pickupLat", order.getBranch().getAddress().getLat());
                tracking.put("pickupLng", order.getBranch().getAddress().getLon());
            }
        }

        if (order.getDeliveryAddress() != null) {
            tracking.put("deliveryAddress", order.getDeliveryAddress().getAddress());
            tracking.put("deliveryLat", order.getDeliveryAddress().getLat());
            tracking.put("deliveryLng", order.getDeliveryAddress().getLon());
        }

        Raider raider = order.getRaider();
        boolean isLiveTrackingActive = order.getStatus() == OrderStatus.ASSIGNED
                || order.getStatus() == OrderStatus.PICKED_UP;

        tracking.put("isLiveTrackingActive", isLiveTrackingActive);

        if (raider != null) {
            tracking.put("raiderId", raider.getId());
            tracking.put("raiderName", raider.getAppUser() != null ? raider.getAppUser().getDisplayName() : "Delivery Partner");
            tracking.put("raiderPhone", raider.getAppUser() != null ? raider.getAppUser().getUsername() : null);
            tracking.put("vehicleType", raider.getVehicleType() != null ? raider.getVehicleType() : "BIKE");
            tracking.put("vehicleNumber", raider.getVehicleNumber());

            Double lat = raider.getCurrentLat();
            Double lng = raider.getCurrentLng();
            if (lat == null || lng == null) {
                Optional<RaiderLiveLocation> locOpt = raiderLiveLocationRepo.findByRaider(raider);
                if (locOpt.isPresent()) {
                    lat = locOpt.get().getLat();
                    lng = locOpt.get().getLng();
                }
            }
            tracking.put("riderLat", lat);
            tracking.put("riderLng", lng);
        }

        return tracking;
    }
}