package com.company.efood.user.services.servicesimpl;

import com.company.efood.config.SSLCommerzConfig;
import com.company.efood.sys.dto.PaymentDto;
import com.company.efood.sys.entity.Order;
import com.company.efood.sys.entity.Payment;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.sys.repository.PaymentRepo;
import com.company.efood.sys.utils.PaymentMethod;
import com.company.efood.sys.utils.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private SSLCommerzConfig config;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void shouldCreateCodPaymentWhenPaymentMethodIsCod() {
        Order order = new Order();
        order.setId(10L);
        order.setTotalAmount(BigDecimal.valueOf(250.5));

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setOrderId(10L);
        paymentDto.setPaymentMethod("COD");

        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(paymentRepo.findByOrderId(10L)).thenReturn(Optional.empty());
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = paymentService.initiatePayment(paymentDto, 99L);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepo).save(captor.capture());
        Payment savedPayment = captor.getValue();

        assertTrue(response.contains("Cash on delivery"));
        assertEquals(PaymentMethod.COD, savedPayment.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
    }
}
