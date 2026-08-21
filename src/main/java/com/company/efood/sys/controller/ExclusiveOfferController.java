package com.company.efood.sys.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.ExclusiveOfferDto;
import com.company.efood.sys.services.ExclusiveOfferService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@RestController
@AllArgsConstructor
public class ExclusiveOfferController {

    private final ExclusiveOfferService exclusiveOfferService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    // Public endpoint for customer app & web visitors
    @GetMapping(PUBLIC_ENDPOINT + "offers")
    public BaseResponse getActiveOffers() {
        try {
            return baseUtils.generateSuccessResponse(exclusiveOfferService.getActiveOffers(), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    // Admin endpoints
    @PostMapping(PRIVET_ENDPOINT + "sya/offers/pageable-data")
    public BaseResponse getPageableOffers(@RequestBody @Valid BasePageableRequest request) {
        try {
            return baseUtils.generateSuccessResponse(exclusiveOfferService.getPageableOffers(request), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping(PRIVET_ENDPOINT + "sya/offers")
    public BaseResponse saveOffer(@RequestBody @Valid ExclusiveOfferDto dto, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(exclusiveOfferService.save(dto, userId), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PutMapping(PRIVET_ENDPOINT + "sya/offers")
    public BaseResponse updateOffer(@RequestBody @Valid ExclusiveOfferDto dto, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            return baseUtils.generateSuccessResponse(exclusiveOfferService.save(dto, userId), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @DeleteMapping(PRIVET_ENDPOINT + "sya/offers/{id}")
    public BaseResponse deleteOffer(@PathVariable Long id) {
        try {
            return baseUtils.generateSuccessResponse(exclusiveOfferService.delete(id), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
