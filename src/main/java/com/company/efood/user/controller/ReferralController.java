package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.user.dto.ReferralInfoDto;
import com.company.efood.user.services.ReferralService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "referral")
@AllArgsConstructor
public class ReferralController {

    private final ReferralService referralService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @GetMapping("/my-code")
    public BaseResponse getMyReferralInfo(HttpServletRequest request) {
        try {
            Long customerId = CurrentUserContext.getReferenceId();
            if (customerId == null) {
                customerId = authTokenUtils.getUserIdFromRequest(request);
            }
            ReferralInfoDto info = referralService.getReferralInfo(customerId);
            return baseUtils.generateSuccessResponse(info, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/validate")
    public BaseResponse validateReferralCode(@RequestParam("code") String code) {
        try {
            Optional<Long> referrerId = referralService.validateCode(code);
            boolean isValid = referrerId.isPresent();
            return baseUtils.generateSuccessResponse(
                    Map.of("isValid", isValid, "code", code),
                    PROCESS_COMPLETE,
                    PROCESS_COMPLETE_BN
            );
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
