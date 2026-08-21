package com.company.efood.user.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.user.dto.LoyaltyRedeemQuoteDto;
import com.company.efood.user.services.LoyaltyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "loyalty")
@AllArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @GetMapping("/summary")
    public BaseResponse getLoyaltySummary(HttpServletRequest request) {
        try {
            Long customerId = CurrentUserContext.getReferenceId();
            if (customerId == null) {
                customerId = authTokenUtils.getUserIdFromRequest(request);
            }
            return baseUtils.generateSuccessResponse(
                    loyaltyService.getLoyaltySummary(customerId),
                    PROCESS_COMPLETE,
                    PROCESS_COMPLETE_BN
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/history")
    public BaseResponse getLoyaltyHistory(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            Long customerId = CurrentUserContext.getReferenceId();
            if (customerId == null) {
                customerId = authTokenUtils.getUserIdFromRequest(request);
            }
            return baseUtils.generateSuccessResponse(
                    loyaltyService.getLoyaltyHistory(customerId, basePageableRequest),
                    PROCESS_COMPLETE,
                    PROCESS_COMPLETE_BN
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/quote-discount")
    public BaseResponse calculateDiscountQuote(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            Long customerId = CurrentUserContext.getReferenceId();
            if (customerId == null) {
                customerId = authTokenUtils.getUserIdFromRequest(request);
            }
            Integer requestedPoints = body.get("requestedPoints") != null
                    ? Integer.parseInt(body.get("requestedPoints").toString())
                    : null;
            BigDecimal orderTotal = body.get("orderTotal") != null
                    ? new BigDecimal(body.get("orderTotal").toString())
                    : BigDecimal.ZERO;

            LoyaltyRedeemQuoteDto quote = loyaltyService.calculateDiscountQuote(customerId, requestedPoints, orderTotal);
            return baseUtils.generateSuccessResponse(quote, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
