package com.company.efood.user.controller;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@AllArgsConstructor
@RestController
@RequestMapping(PUBLIC_ENDPOINT+PRODUCT_END_POINT)
public class PublicProductController {
    private final ProductService productService;
    private final BaseUtils baseUtils;

    @PostMapping("/list")
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest) {
        try {
            return baseUtils.generateSuccessResponse(productService.getPageableProductByBranch(basePageableRequest, basePageableRequest.getIntParam1()), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/branch/{branchId}")
    public BaseResponse getProductsByBranch(
            @PathVariable Long branchId,
            @RequestParam(value = "productType", required = false) String productType,
            @RequestParam(value = "serviceType", required = false) String serviceType) {
        try {
            BasePageableRequest request = new BasePageableRequest();
            request.setPage(0);
            request.setSize(50);
            request.setStringParam1(productType);
            request.setStringParam2(serviceType);
            return baseUtils.generateSuccessResponse(productService.getPageableProductByBranch(request, branchId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/by-service")
    public BaseResponse getProductsByService(
            @RequestParam(value = "serviceType", required = false) String serviceType,
            @RequestParam(value = "productType", required = false) String productType,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {
        try {
            return baseUtils.generateSuccessResponse(
                    productService.getProductsWithFilters(branchId, categoryId, productType, serviceType, search, page, size),
                    PROCESS_COMPLETE,
                    PROCESS_COMPLETE_BN
            );
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
