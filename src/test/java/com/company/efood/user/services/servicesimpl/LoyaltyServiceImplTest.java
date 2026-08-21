package com.company.efood.user.services.servicesimpl;

import com.company.efood.base.BaseUtils;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.user.dto.LoyaltyRedeemQuoteDto;
import com.company.efood.user.dto.LoyaltySummaryDto;
import com.company.efood.user.entity.Customer;
import com.company.efood.user.entity.LoyaltyAccount;
import com.company.efood.user.entity.LoyaltyTransaction;
import com.company.efood.user.entity.LoyaltyTransactionType;
import com.company.efood.user.repository.CustomerRepo;
import com.company.efood.user.repository.LoyaltyAccountRepo;
import com.company.efood.user.repository.LoyaltyTransactionRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    @Mock
    private LoyaltyAccountRepo loyaltyAccountRepo;

    @Mock
    private LoyaltyTransactionRepo loyaltyTransactionRepo;

    @Mock
    private CustomerRepo customerRepo;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private BaseUtils baseUtils;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    private Customer testCustomer;
    private Order testOrder;
    private LoyaltyAccount testAccount;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loyaltyService, "pointsPerHundredSpend", 1);
        ReflectionTestUtils.setField(loyaltyService, "currencyPerPoint", 1.0);
        ReflectionTestUtils.setField(loyaltyService, "minPointsToRedeem", 10);
        ReflectionTestUtils.setField(loyaltyService, "maxDiscountPercentage", 50);
        ReflectionTestUtils.setField(loyaltyService, "expiryDays", 365);

        testCustomer = new Customer();
        testCustomer.setId(100L);
        testCustomer.setFullName("Test Customer");

        testOrder = new Order();
        testOrder.setId(500L);
        testOrder.setCustomer(testCustomer);
        testOrder.setTotalAmount(BigDecimal.valueOf(550.0));

        testAccount = new LoyaltyAccount(testCustomer);
        testAccount.setCurrentPoints(100);
        testAccount.setLifetimePointsEarned(200);
    }

    @Test
    void shouldAwardPointsCorrectlyOnCompletedOrder() {
        when(loyaltyTransactionRepo.existsByOrderIdAndType(500L, LoyaltyTransactionType.EARNED)).thenReturn(false);
        when(orderRepo.findById(500L)).thenReturn(Optional.of(testOrder));
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyAccountRepo.findByCustomerId(100L)).thenReturn(Optional.of(testAccount));

        loyaltyService.awardPointsForOrder(500L);

        // 550 BDT spend at 1 point per 100 BDT = 5 points
        assertEquals(105, testAccount.getCurrentPoints());
        assertEquals(205, testAccount.getLifetimePointsEarned());
        verify(loyaltyAccountRepo).save(testAccount);
        verify(loyaltyTransactionRepo).save(any(LoyaltyTransaction.class));
    }

    @Test
    void shouldNotAwardPointsTwiceForSameOrder() {
        // Idempotency: points already awarded for order #500
        when(loyaltyTransactionRepo.existsByOrderIdAndType(500L, LoyaltyTransactionType.EARNED)).thenReturn(true);

        loyaltyService.awardPointsForOrder(500L);

        verify(orderRepo, never()).findById(any());
        verify(loyaltyAccountRepo, never()).save(any());
        verify(loyaltyTransactionRepo, never()).save(any());
    }

    @Test
    void shouldReverseEarnedPointsWhenOrderIsCancelled() {
        LoyaltyTransaction earnedTx = new LoyaltyTransaction(testCustomer, testOrder, LoyaltyTransactionType.EARNED, 10, "Earned points");

        when(orderRepo.findById(500L)).thenReturn(Optional.of(testOrder));
        when(loyaltyTransactionRepo.findByOrderIdAndType(500L, LoyaltyTransactionType.EARNED)).thenReturn(Optional.of(earnedTx));
        when(loyaltyTransactionRepo.existsByOrderIdAndType(500L, LoyaltyTransactionType.REVERSED)).thenReturn(false);
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyAccountRepo.findByCustomerId(100L)).thenReturn(Optional.of(testAccount));

        loyaltyService.reversePointsForOrder(500L);

        // 100 current points minus 10 reversed points = 90
        assertEquals(90, testAccount.getCurrentPoints());
        verify(loyaltyAccountRepo).save(testAccount);
        verify(loyaltyTransactionRepo).save(any(LoyaltyTransaction.class));
    }

    @Test
    void shouldRefundRedeemedPointsWhenOrderIsCancelled() {
        LoyaltyTransaction redeemedTx = new LoyaltyTransaction(testCustomer, testOrder, LoyaltyTransactionType.REDEEMED, -30, "Redeemed points");

        when(orderRepo.findById(500L)).thenReturn(Optional.of(testOrder));
        when(loyaltyTransactionRepo.findByOrderIdAndType(500L, LoyaltyTransactionType.EARNED)).thenReturn(Optional.empty());
        when(loyaltyTransactionRepo.findByOrderIdAndType(500L, LoyaltyTransactionType.REDEEMED)).thenReturn(Optional.of(redeemedTx));
        when(loyaltyTransactionRepo.existsByOrderIdAndType(500L, LoyaltyTransactionType.ADJUSTED)).thenReturn(false);
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyAccountRepo.findByCustomerId(100L)).thenReturn(Optional.of(testAccount));

        loyaltyService.reversePointsForOrder(500L);

        // 100 current points + 30 refunded points = 130
        assertEquals(130, testAccount.getCurrentPoints());
        verify(loyaltyAccountRepo).save(testAccount);
    }

    @Test
    void shouldRedeemPointsSuccessfullyWithPessimisticLock() {
        when(loyaltyAccountRepo.findByCustomerIdWithLock(100L)).thenReturn(Optional.of(testAccount));

        BigDecimal discount = loyaltyService.redeemPointsForOrder(100L, 500L, 40, BigDecimal.valueOf(200.0));

        assertEquals(BigDecimal.valueOf(40.0).setScale(2), discount);
        assertEquals(60, testAccount.getCurrentPoints());
        verify(loyaltyAccountRepo).save(testAccount);
        verify(loyaltyTransactionRepo).save(any(LoyaltyTransaction.class));
    }

    @Test
    void shouldRejectRedeemWhenBalanceInsufficient() {
        when(loyaltyAccountRepo.findByCustomerIdWithLock(100L)).thenReturn(Optional.of(testAccount));

        assertThrows(IllegalArgumentException.class, () -> {
            loyaltyService.redeemPointsForOrder(100L, 500L, 150, BigDecimal.valueOf(500.0));
        });
    }

    @Test
    void shouldCalculateDiscountQuoteAccurately() {
        when(customerRepo.findById(100L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyAccountRepo.findByCustomerId(100L)).thenReturn(Optional.of(testAccount));

        // Order total 100, max discount 50% = 50 BDT, available 100 points
        LoyaltyRedeemQuoteDto quote = loyaltyService.calculateDiscountQuote(100L, 100, BigDecimal.valueOf(100.0));

        assertTrue(quote.getIsValid());
        assertEquals(50, quote.getEligiblePoints());
        assertEquals(BigDecimal.valueOf(50.0).setScale(2), quote.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(50.0).setScale(2), quote.getNewTotalAmount());
    }
}
