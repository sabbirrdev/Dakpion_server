package com.company.dakpion.dakpion.service;
import com.company.dakpion.dakpion.dto.PaymentDto;

import java.util.Map;

public interface PaymentService {

     String initiatePayment(PaymentDto paymentDto, Long userId);
     void handleSuccess(Map<String, String> params);
}

