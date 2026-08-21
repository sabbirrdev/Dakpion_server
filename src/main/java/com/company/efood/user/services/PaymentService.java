package com.company.efood.user.services;


import com.company.efood.sys.dto.PaymentDto;

import java.util.Map;

public interface PaymentService {

     String initiatePayment(PaymentDto paymentDto, Long userId);
     void handleSuccess(Map<String, String> params);
     String markCodCollected(Long orderId, Long userId);

}

