package com.company.efood.user.controller;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.catalog.service.CatalogProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@AllArgsConstructor
@RestController
@RequestMapping(PUBLIC_ENDPOINT + "catalog")
public class PublicCatalogController {

    private final CatalogProductService catalogProductService;
    private final BaseUtils baseUtils;

    @PostMapping("/products")
    public BaseResponse getCatalogProducts(@Valid @RequestBody BasePageableRequest basePageableRequest) {
        try {
            return baseUtils.generateSuccessResponse(catalogProductService.getPageableAllData(basePageableRequest, 0L), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
