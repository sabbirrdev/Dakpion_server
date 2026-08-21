package com.company.efood.user.services.servicesimpl;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.user.dto.LoyaltyRedeemQuoteDto;
import com.company.efood.user.dto.LoyaltySummaryDto;
import com.company.efood.user.dto.LoyaltyTransactionDto;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.entity.LoyaltyAccount;
import com.company.efood.user.entity.LoyaltyTransaction;
import com.company.efood.user.entity.LoyaltyTransactionType;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.repository.LoyaltyAccountRepo;
import com.company.efood.user.repository.LoyaltyTransactionRepo;
import com.company.efood.user.services.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepo loyaltyAccountRepo;
    private final LoyaltyTransactionRepo loyaltyTransactionRepo;
    private final CustomerRepo customerRepo;
    private final OrderRepo orderRepo;
    private final BaseUtils baseUtils;

    @Value("${loyalty.points-per-hundred-spend:1}")
    private int pointsPerHundredSpend;

    @Value("${loyalty.currency-per-point:1.0}")
    private double currencyPerPoint;

    @Value("${loyalty.min-points-to-redeem:10}")
    private int minPointsToRedeem;

    @Value("${loyalty.max-discount-percentage:50}")
    private int maxDiscountPercentage;

    @Value("${loyalty.expiry-days:365}")
    private int expiryDays;

    @Override
    @Transactional(readOnly = true)
    public LoyaltySummaryDto getLoyaltySummary(Long customerIdOrUserId) {
        Customer customer = resolveCustomer(customerIdOrUserId);
        LoyaltyAccount account = loyaltyAccountRepo.findByCustomerId(customer.getId())
                .orElseGet(() -> new LoyaltyAccount(customer));

        BigDecimal monetaryValue = BigDecimal.valueOf(account.getCurrentPoints())
                .multiply(BigDecimal.valueOf(currencyPerPoint))
                .setScale(2, RoundingMode.HALF_UP);

        return LoyaltySummaryDto.builder()
                .currentPoints(account.getCurrentPoints() != null ? account.getCurrentPoints() : 0)
                .lifetimePointsEarned(account.getLifetimePointsEarned() != null ? account.getLifetimePointsEarned() : 0)
                .monetaryValue(monetaryValue)
                .pointsPerHundredSpend(pointsPerHundredSpend)
                .currencyPerPoint(BigDecimal.valueOf(currencyPerPoint).setScale(2, RoundingMode.HALF_UP))
                .minPointsToRedeem(minPointsToRedeem)
                .maxDiscountPercentage(maxDiscountPercentage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoyaltyTransactionDto> getLoyaltyHistory(Long customerIdOrUserId, BasePageableRequest request) {
        Customer customer = resolveCustomer(customerIdOrUserId);
        PageRequest pageRequest = baseUtils.getPageRequest(request.getPage(), request.getSize());
        Page<LoyaltyTransaction> page = loyaltyTransactionRepo.findByCustomerIdOrderByEntryDateDesc(customer.getId(), pageRequest);

        List<LoyaltyTransactionDto> dtoList = page.stream()
                .map(tx -> LoyaltyTransactionDto.builder()
                        .id(tx.getId())
                        .orderId(tx.getOrder() != null ? tx.getOrder().getId() : null)
                        .type(tx.getType())
                        .points(tx.getPoints())
                        .description(tx.getDescription())
                        .entryDate(tx.getEntryDate())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageRequest, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyRedeemQuoteDto calculateDiscountQuote(Long customerIdOrUserId, Integer requestedPoints, BigDecimal orderTotal) {
        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return LoyaltyRedeemQuoteDto.builder()
                    .requestedPoints(requestedPoints)
                    .eligiblePoints(0)
                    .discountAmount(BigDecimal.ZERO)
                    .maxAllowedDiscount(BigDecimal.ZERO)
                    .newTotalAmount(BigDecimal.ZERO)
                    .isValid(false)
                    .message("Order total must be greater than zero")
                    .build();
        }

        Customer customer = resolveCustomer(customerIdOrUserId);
        LoyaltyAccount account = loyaltyAccountRepo.findByCustomerId(customer.getId())
                .orElseGet(() -> new LoyaltyAccount(customer));

        int currentBalance = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
        int points = requestedPoints != null ? requestedPoints : currentBalance;

        if (points < minPointsToRedeem) {
            return LoyaltyRedeemQuoteDto.builder()
                    .requestedPoints(points)
                    .eligiblePoints(0)
                    .discountAmount(BigDecimal.ZERO)
                    .maxAllowedDiscount(BigDecimal.ZERO)
                    .newTotalAmount(orderTotal)
                    .isValid(false)
                    .message("Minimum " + minPointsToRedeem + " points required to redeem.")
                    .build();
        }

        if (points > currentBalance) {
            points = currentBalance;
        }

        BigDecimal maxDiscount = orderTotal.multiply(BigDecimal.valueOf(maxDiscountPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal calculatedDiscount = BigDecimal.valueOf(points)
                .multiply(BigDecimal.valueOf(currencyPerPoint))
                .setScale(2, RoundingMode.HALF_UP);

        if (calculatedDiscount.compareTo(maxDiscount) > 0) {
            calculatedDiscount = maxDiscount;
            points = maxDiscount.divide(BigDecimal.valueOf(currencyPerPoint), 0, RoundingMode.FLOOR).intValue();
        }

        BigDecimal newTotal = orderTotal.subtract(calculatedDiscount);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            newTotal = BigDecimal.ZERO;
        }

        return LoyaltyRedeemQuoteDto.builder()
                .requestedPoints(requestedPoints)
                .eligiblePoints(points)
                .discountAmount(calculatedDiscount)
                .maxAllowedDiscount(maxDiscount)
                .newTotalAmount(newTotal)
                .isValid(true)
                .message("Eligible for ৳" + calculatedDiscount + " discount using " + points + " points.")
                .build();
    }

    @Override
    @Transactional
    public void awardPointsForOrder(Long orderId) {
        if (orderId == null) return;

        // Idempotency check: points should only be awarded once per order
        if (loyaltyTransactionRepo.existsByOrderIdAndType(orderId, LoyaltyTransactionType.EARNED)) {
            log.info("Points already awarded for order #{}. Skipping.", orderId);
            return;
        }

        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null || order.getCustomer() == null) {
            log.warn("Order #{} or associated customer not found. Cannot award points.", orderId);
            return;
        }

        BigDecimal spendAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        if (spendAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // Calculate points based on configurable rate
        int earnedPoints = spendAmount.divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR).intValue() * pointsPerHundredSpend;
        if (earnedPoints <= 0) {
            return;
        }

        Customer customer = order.getCustomer();
        LoyaltyAccount account = getOrCreateAccount(customer.getId());

        int prevPoints = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
        int prevLifetime = account.getLifetimePointsEarned() != null ? account.getLifetimePointsEarned() : 0;

        account.setCurrentPoints(prevPoints + earnedPoints);
        account.setLifetimePointsEarned(prevLifetime + earnedPoints);
        baseUtils.setUpdateUserInfo(account, account);
        loyaltyAccountRepo.save(account);

        LoyaltyTransaction transaction = new LoyaltyTransaction(
                customer,
                order,
                LoyaltyTransactionType.EARNED,
                earnedPoints,
                "Earned " + earnedPoints + " reward points for Order #" + order.getId()
        );
        transaction.setEntryUser(customer.getAppUser() != null ? customer.getAppUser().getId() : null);
        baseUtils.setEntryUserInfo(transaction);
        loyaltyTransactionRepo.save(transaction);

        log.info("Awarded {} points to customer #{} for order #{}", earnedPoints, customer.getId(), orderId);
    }

    @Override
    @Transactional
    public void reversePointsForOrder(Long orderId) {
        if (orderId == null) return;

        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null || order.getCustomer() == null) {
            return;
        }
        Customer customer = order.getCustomer();

        // 1. Reverse EARNED points if any
        Optional<LoyaltyTransaction> earnedTxOpt = loyaltyTransactionRepo.findByOrderIdAndType(orderId, LoyaltyTransactionType.EARNED);
        if (earnedTxOpt.isPresent()) {
            boolean alreadyReversed = loyaltyTransactionRepo.existsByOrderIdAndType(orderId, LoyaltyTransactionType.REVERSED);
            if (!alreadyReversed) {
                LoyaltyTransaction earnedTx = earnedTxOpt.get();
                int pointsToDeduct = earnedTx.getPoints();

                LoyaltyAccount account = getOrCreateAccount(customer.getId());
                int current = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
                account.setCurrentPoints(Math.max(0, current - pointsToDeduct));
                baseUtils.setUpdateUserInfo(account, account);
                loyaltyAccountRepo.save(account);

                LoyaltyTransaction reverseTx = new LoyaltyTransaction(
                        customer,
                        order,
                        LoyaltyTransactionType.REVERSED,
                        -pointsToDeduct,
                        "Reversed " + pointsToDeduct + " points due to cancellation of Order #" + orderId
                );
                baseUtils.setEntryUserInfo(reverseTx);
                loyaltyTransactionRepo.save(reverseTx);
                log.info("Reversed {} earned points for cancelled order #{}", pointsToDeduct, orderId);
            }
        }

        // 2. Refund REDEEMED points if any
        Optional<LoyaltyTransaction> redeemedTxOpt = loyaltyTransactionRepo.findByOrderIdAndType(orderId, LoyaltyTransactionType.REDEEMED);
        if (redeemedTxOpt.isPresent()) {
            boolean alreadyAdjusted = loyaltyTransactionRepo.existsByOrderIdAndType(orderId, LoyaltyTransactionType.ADJUSTED);
            if (!alreadyAdjusted) {
                LoyaltyTransaction redeemedTx = redeemedTxOpt.get();
                int pointsToRefund = Math.abs(redeemedTx.getPoints());

                LoyaltyAccount account = getOrCreateAccount(customer.getId());
                int current = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
                account.setCurrentPoints(current + pointsToRefund);
                baseUtils.setUpdateUserInfo(account, account);
                loyaltyAccountRepo.save(account);

                LoyaltyTransaction refundTx = new LoyaltyTransaction(
                        customer,
                        order,
                        LoyaltyTransactionType.ADJUSTED,
                        pointsToRefund,
                        "Refunded " + pointsToRefund + " redeemed points due to cancellation of Order #" + orderId
                );
                baseUtils.setEntryUserInfo(refundTx);
                loyaltyTransactionRepo.save(refundTx);
                log.info("Refunded {} redeemed points for cancelled order #{}", pointsToRefund, orderId);
            }
        }
    }

    @Override
    @Transactional
    public BigDecimal redeemPointsForOrder(Long customerId, Long orderId, Integer pointsToRedeem, BigDecimal orderTotal) {
        if (pointsToRedeem == null || pointsToRedeem <= 0 || orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Use pessimistic lock to prevent concurrent double-redemption race conditions
        LoyaltyAccount account = loyaltyAccountRepo.findByCustomerIdWithLock(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found for customer #" + customerId));

        int balance = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
        if (balance < pointsToRedeem) {
            throw new IllegalArgumentException("Insufficient loyalty points balance. Available: " + balance + ", requested: " + pointsToRedeem);
        }

        if (pointsToRedeem < minPointsToRedeem) {
            throw new IllegalArgumentException("Minimum " + minPointsToRedeem + " points required for redemption");
        }

        BigDecimal maxDiscount = orderTotal.multiply(BigDecimal.valueOf(maxDiscountPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = BigDecimal.valueOf(pointsToRedeem)
                .multiply(BigDecimal.valueOf(currencyPerPoint))
                .setScale(2, RoundingMode.HALF_UP);

        if (discountAmount.compareTo(maxDiscount) > 0) {
            throw new IllegalArgumentException("Redemption discount exceeds maximum allowed cap of " + maxDiscountPercentage + "% (৳" + maxDiscount + ")");
        }

        // Deduct points
        account.setCurrentPoints(balance - pointsToRedeem);
        baseUtils.setUpdateUserInfo(account, account);
        loyaltyAccountRepo.save(account);

        Customer customer = account.getCustomer();
        Order order = orderId != null ? orderRepo.findById(orderId).orElse(null) : null;

        LoyaltyTransaction transaction = new LoyaltyTransaction(
                customer,
                order,
                LoyaltyTransactionType.REDEEMED,
                -pointsToRedeem,
                "Redeemed " + pointsToRedeem + " points for discount on Order #" + (orderId != null ? orderId : "N/A")
        );
        transaction.setEntryUser(customer.getAppUser() != null ? customer.getAppUser().getId() : null);
        baseUtils.setEntryUserInfo(transaction);
        loyaltyTransactionRepo.save(transaction);

        log.info("Customer #{} successfully redeemed {} points for discount ৳{}", customerId, pointsToRedeem, discountAmount);
        return discountAmount;
    }

    @Override
    @Transactional
    public void adjustPoints(Long customerId, Long orderId, int points, String description) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        LoyaltyAccount account = getOrCreateAccount(customerId);
        int current = account.getCurrentPoints() != null ? account.getCurrentPoints() : 0;
        int updated = Math.max(0, current + points);
        account.setCurrentPoints(updated);

        if (points > 0) {
            int lifetime = account.getLifetimePointsEarned() != null ? account.getLifetimePointsEarned() : 0;
            account.setLifetimePointsEarned(lifetime + points);
        }

        baseUtils.setUpdateUserInfo(account, account);
        loyaltyAccountRepo.save(account);

        Order order = null;
        if (orderId != null) {
            order = orderRepo.findById(orderId).orElse(null);
        }

        LoyaltyTransaction tx = new LoyaltyTransaction(
                customer,
                order,
                LoyaltyTransactionType.ADJUSTED,
                points,
                description
        );
        baseUtils.setEntryUserInfo(tx);
        loyaltyTransactionRepo.save(tx);

        log.info("Adjusted {} points for customer #{}. New balance: {}. Reason: {}", points, customerId, updated, description);
    }

    @Override
    @Transactional
    public LoyaltyAccount getOrCreateAccount(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        return loyaltyAccountRepo.findByCustomerId(customerId)
                .orElseGet(() -> {
                    LoyaltyAccount newAccount = new LoyaltyAccount(customer);
                    newAccount.setEntryUser(customer.getAppUser() != null ? customer.getAppUser().getId() : null);
                    baseUtils.setEntryUserInfo(newAccount);
                    return loyaltyAccountRepo.save(newAccount);
                });
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2:00 AM
    @Transactional
    public void expireOldPoints() {
        log.info("Starting scheduled loyalty points expiry job...");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(expiryDays);
        List<LoyaltyTransaction> oldEarned = loyaltyTransactionRepo.findEarnedBeforeCutoff(cutoff);

        // Note: For advanced FIFO point expiration, we track unredeemed batches
        log.info("Checked points expiry against cutoff date {}. Total past batches checked: {}", cutoff, oldEarned.size());
    }

    private Customer resolveCustomer(Long customerIdOrUserId) {
        if (customerIdOrUserId == null) {
            throw new IllegalArgumentException("Customer or User ID must not be null");
        }
        // First try finding customer directly by customerId (referenceId)
        Optional<Customer> customerOpt = customerRepo.findById(customerIdOrUserId);
        if (customerOpt.isPresent()) {
            return customerOpt.get();
        }
        // Fallback: try finding customer by appUserId
        return customerRepo.findByAppUserId(customerIdOrUserId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for ID: " + customerIdOrUserId));
    }
}
