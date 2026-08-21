package com.company.efood.sys.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.raider.repository.RaiderRepo;
import com.company.efood.seller.repository.SellerRepo;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.sys.repository.ProductRepo;
import com.company.efood.sys.repository.ShopRepo;
import com.company.efood.sys.utils.OrderStatus;
import com.company.efood.user.repository.CustomerRepo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;
import static com.company.efood.base.BaseConstants.SYSTEM_ADMIN_END_POINT;

/**
 * Aggregated counters for the admin dashboard landing page:
 * total riders / sellers / customers, today's orders, pending approvals, etc.
 */
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "dashboard")
@AllArgsConstructor
public class DashboardController {

    private final CustomerRepo customerRepo;
    private final SellerRepo sellerRepo;
    private final RaiderRepo raiderRepo;
    private final ShopRepo shopRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final BaseUtils baseUtils;
    private final com.company.efood.user.repository.CustomerVisitorLogRepo customerVisitorLogRepo;

    @GetMapping("/summary")
    public BaseResponse getSummary() {
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

            List<Order> todayOrders = orderRepo.findByEntryDateBetween(startOfDay, endOfDay);

            long todayDeliveredOrCompleted = todayOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .count();

            long todayCancelled = todayOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
                    .count();

            BigDecimal todayRevenue = todayOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                    .map(Order::getTotalAmount)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long visitorCount = customerVisitorLogRepo.countDistinctVisitors();

            Map<String, Object> payload = new HashMap<>();
            payload.put("totalCustomers", customerRepo.count());
            payload.put("totalSellers", sellerRepo.count());
            payload.put("totalRiders", raiderRepo.count());
            payload.put("totalShops", shopRepo.count());
            payload.put("totalProducts", productRepo.count());
            payload.put("totalOrders", orderRepo.count());

            payload.put("todayOrders", (long) todayOrders.size());
            payload.put("todayDeliveredOrders", todayDeliveredOrCompleted);
            payload.put("todayCancelledOrders", todayCancelled);
            payload.put("todayRevenue", todayRevenue);

            payload.put("pendingSellerCount", sellerRepo.findAll().stream().filter(s -> !Boolean.TRUE.equals(s.getIsApproved())).count());
            payload.put("pendingRaiderCount", raiderRepo.findAll().stream().filter(r -> !Boolean.TRUE.equals(r.getIsApproved())).count());
            payload.put("pendingShopCount", shopRepo.findAll().stream().filter(s -> !Boolean.TRUE.equals(s.getIsApproved())).count());
            payload.put("pendingProductCount", productRepo.findAll().stream().filter(p -> !Boolean.TRUE.equals(p.getIsApproved())).count());

            payload.put("totalVisitors", visitorCount);

            return baseUtils.generateSuccessResponse(payload, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/analytics")
    public BaseResponse getAnalytics() {
        try {
            List<Order> allOrders = orderRepo.findAll();

            BigDecimal totalRevenue = allOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.COMPLETED)
                    .map(Order::getTotalAmount)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDeliveryFee = allOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.COMPLETED)
                    .map(Order::getDeliveryFee)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 7-day revenue trend
            List<Map<String, Object>> dailyTrend = new java.util.ArrayList<>();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMM dd");

            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = start.plusDays(1).minusNanos(1);

                List<Order> dayOrders = allOrders.stream()
                        .filter(o -> o.getEntryDate() != null && !o.getEntryDate().isBefore(start) && !o.getEntryDate().isAfter(end))
                        .collect(java.util.stream.Collectors.toList());

                BigDecimal dayRev = dayOrders.stream()
                        .filter(o -> o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.COMPLETED)
                        .map(Order::getTotalAmount)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Map<String, Object> dayMap = new HashMap<>();
                dayMap.put("date", date.format(fmt));
                dayMap.put("revenue", dayRev);
                dayMap.put("orders", dayOrders.size());
                dailyTrend.add(dayMap);
            }

            // Order status distribution
            Map<String, Long> statusCount = new HashMap<>();
            for (OrderStatus s : OrderStatus.values()) {
                long count = allOrders.stream().filter(o -> o.getStatus() == s).count();
                statusCount.put(s.name(), count);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("totalRevenue", totalRevenue);
            data.put("totalDeliveryFee", totalDeliveryFee);
            data.put("totalOrders", allOrders.size());
            data.put("dailyTrend", dailyTrend);
            data.put("statusDistribution", statusCount);

            return baseUtils.generateSuccessResponse(data, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
