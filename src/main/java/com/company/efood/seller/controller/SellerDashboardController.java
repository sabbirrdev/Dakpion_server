package com.company.efood.seller.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.seller.entity.Seller;
import com.company.efood.seller.repository.SellerRepo;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.sys.utils.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;
import static com.company.efood.base.BaseConstants.SELLER_END_POINT;

@Slf4j
@RestController
@RequestMapping({SELLER_END_POINT + "dashboard", "/api/private/seller/dashboard", "/seller/dashboard"})
@RequiredArgsConstructor
public class SellerDashboardController {

    private final OrderRepo orderRepo;
    private final SellerRepo sellerRepo;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @GetMapping("/metrics")
    public ResponseEntity<BaseResponse> getSellerMetrics(HttpServletRequest request) {
        try {
            Long rawUserId = authTokenUtils.getUserIdFromRequest(request);
            if (rawUserId == null) {
                rawUserId = CurrentUserContext.getReferenceId();
            }
            final Long finalUserId = rawUserId;

            Seller seller = sellerRepo.findByAppUserId(finalUserId).orElse(null);
            final Long finalSellerId = seller != null ? seller.getId() : finalUserId;

            List<Order> allSellerOrders = orderRepo.findAll().stream()
                    .filter(o -> o.getBranch() != null && o.getBranch().getShop() != null &&
                            ((o.getBranch().getShop().getSeller() != null && (Objects.equals(o.getBranch().getShop().getSeller().getId(), finalSellerId) || (o.getBranch().getShop().getSeller().getAppUser() != null && Objects.equals(o.getBranch().getShop().getSeller().getAppUser().getId(), finalUserId))))
                            || Objects.equals(o.getEntryUser(), finalUserId)))
                    .collect(Collectors.toList());

            long totalOrders = allSellerOrders.size();
            long completedOrders = allSellerOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.DELIVERED).count();
            long activeOrders = allSellerOrders.stream().filter(o -> o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.ACCEPTED || o.getStatus() == OrderStatus.PREPARING || o.getStatus() == OrderStatus.ASSIGNED || o.getStatus() == OrderStatus.PICKED_UP || o.getStatus() == OrderStatus.ON_THE_WAY).count();
            long cancelledOrders = allSellerOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REJECTED).count();

            BigDecimal totalRevenue = allSellerOrders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.DELIVERED)
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Estimated Net Profit (Revenue minus 10% platform commission and delivery fee)
            BigDecimal platformCommission = totalRevenue.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netProfit = totalRevenue.subtract(platformCommission).setScale(2, RoundingMode.HALF_UP);

            BigDecimal averageOrderValue = completedOrders > 0
                    ? totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // 7-day revenue and order timeline for charts
            List<Map<String, Object>> weeklyChart = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                LocalDateTime dayStart = date.atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1).minusNanos(1);

                List<Order> dayOrders = allSellerOrders.stream()
                        .filter(o -> o.getEntryDate() != null && !o.getEntryDate().isBefore(dayStart) && !o.getEntryDate().isAfter(dayEnd))
                        .collect(Collectors.toList());

                BigDecimal dayRevenue = dayOrders.stream()
                        .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.DELIVERED)
                        .map(Order::getTotalAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Map<String, Object> point = new HashMap<>();
                point.put("date", date.format(formatter));
                point.put("revenue", dayRevenue);
                point.put("orders", dayOrders.size());
                point.put("completed", dayOrders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.DELIVERED).count());
                weeklyChart.add(point);
            }

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalRevenue", totalRevenue);
            metrics.put("netProfit", netProfit);
            metrics.put("platformCommission", platformCommission);
            metrics.put("averageOrderValue", averageOrderValue);
            metrics.put("totalOrders", totalOrders);
            metrics.put("completedOrders", completedOrders);
            metrics.put("activeOrders", activeOrders);
            metrics.put("cancelledOrders", cancelledOrders);
            metrics.put("weeklyChart", weeklyChart);

            return ResponseEntity.ok(baseUtils.generateSuccessResponse(metrics, PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            log.error("Error generating seller metrics", e);
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }
}
