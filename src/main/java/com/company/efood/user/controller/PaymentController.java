package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.PaymentDto;
import com.company.efood.sys.repository.OrderRepo;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.user.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;

@RestController
@RequestMapping(PRIVET_ENDPOINT+"payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping(value = "/pay")
    public BaseResponse initiatePayment(@Valid @RequestBody PaymentDto paymentDto, HttpServletRequest request) {
        try {
            String gatewayUrl = paymentService.initiatePayment(paymentDto, authTokenUtils.getUserIdFromRequest(request));
            return baseUtils.generateSuccessResponse(gatewayUrl, "Payment gateway initialized", "পেমেন্ট গেটওয়ে তৈরি হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse paymentSuccess(@RequestParam Map<String, String> params) {
        try {
            String txnId = params.get("tran_id");
            paymentService.handleSuccess(params);
            return baseUtils.generateSuccessResponse(txnId, "Payment successful", "পেমেন্ট সফল হয়েছে");
        } catch (Exception ex) {
            Logger.getGlobal().info("Payment Exception: " + ex );
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @RequestMapping(value = "/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse paymentFail(@RequestParam Map<String, String> params) {
        return baseUtils.generateErrorResponse(new Exception("Payment failed"));
    }

    @RequestMapping(value = "/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseResponse paymentCancel(@RequestParam Map<String, String> params) {
        return baseUtils.generateErrorResponse(new Exception("Payment cancelled"));
    }
}


