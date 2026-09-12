package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.dto.PaymentDto;
import com.company.dakpion.dakpion.service.PaymentService;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "DakPion Payments", description = "Payment initiation and gateway webhooks (SSLCommerz)")
public class DakpionPaymentController {

    private static final Logger log = Logger.getLogger(DakpionPaymentController.class.getName());

    /** Resolve the frontend origin from the incoming request (e.g. https://dakpion.com). */
    private static final String FRONTEND_ORIGIN = System.getenv().getOrDefault(
            "FRONTEND_ORIGIN", "https://dakpion-client.onrender.com"
    );

    private final PaymentService paymentService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping(value = "/initiate")
    @Operation(summary = "Initiate payment", description = "Generates SSLCommerz gateway redirect session for physical or speed-post letter delivery")
    public BaseResponse initiatePayment(@RequestBody PaymentDto paymentDto, HttpServletRequest request) {
        try {
            String gatewayUrl = paymentService.initiatePayment(paymentDto, authTokenUtils.getUserIdFromRequest(request));
            return baseUtils.generateSuccessResponse(gatewayUrl, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            log.warning("Payment initiation failed: " + ex.getMessage());
            return baseUtils.generateErrorResponse(ex);
        }
    }

    /**
     * SSLCommerz POSTs back here after a successful payment.
     * We validate, update DB, then redirect browser to the frontend success page.
     */
    @RequestMapping(value = "/success", method = {RequestMethod.GET, RequestMethod.POST})
    public void paymentSuccess(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        String orderId = params.get("order_id");
        String tranId  = params.get("tran_id");
        String redirectId = (orderId != null && !orderId.isBlank()) ? orderId : (tranId != null ? tranId : "");

        try {
            paymentService.handleSuccess(params);
            log.info("Payment success handled for tran_id=" + tranId);
            response.sendRedirect(FRONTEND_ORIGIN + "/payment-success?tran_id=" + redirectId + "&order_id=" + redirectId);
        } catch (Exception ex) {
            log.warning("Payment success handler error: " + ex.getMessage());
            response.sendRedirect(FRONTEND_ORIGIN + "/payment-fail?tran_id=" + redirectId + "&order_id=" + redirectId);
        }
    }

    /**
     * SSLCommerz POSTs back here when payment fails.
     */
    @RequestMapping(value = "/fail", method = {RequestMethod.GET, RequestMethod.POST})
    public void paymentFail(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        String orderId = params.getOrDefault("order_id", params.getOrDefault("tran_id", ""));
        log.info("Payment failed for order_id=" + orderId);
        response.sendRedirect(FRONTEND_ORIGIN + "/payment-fail?order_id=" + orderId);
    }

    /**
     * SSLCommerz POSTs back here when user cancels payment.
     */
    @RequestMapping(value = "/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    public void paymentCancel(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        String orderId = params.getOrDefault("order_id", params.getOrDefault("tran_id", ""));
        log.info("Payment cancelled for order_id=" + orderId);
        response.sendRedirect(FRONTEND_ORIGIN + "/payment-cancel?order_id=" + orderId);
    }
}
