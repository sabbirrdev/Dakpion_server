package com.company.efood.seller.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.sys.dto.ProductDto;
import com.company.efood.sys.services.ProductService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(SELLER_END_POINT+SHOP_END_POINT+PRODUCT_END_POINT)
@AllArgsConstructor
public class ProductController implements BaseController<ProductDto> {

    private BaseUtils baseUtils;
    private AuthTokenUtils authTokenUtils;
    private ProductService productService;

    @Override
    @PostMapping()
    public BaseResponse save(@RequestBody @Valid ProductDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping()
    public BaseResponse update(@RequestBody @Valid ProductDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@RequestBody ProductDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.delete(body, authTokenUtils.getUserIdFromRequest(request)), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(GET_OBJECT_BY_ID)
    public BaseResponse getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.getById(id, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(DROPDOWN_LIST_PATH)
    public BaseResponse getDropdownList(HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.getDropdownList(authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(productService.getPageableProductByBranch(basePageableRequest,basePageableRequest.getIntParam1()), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/by-category/{branchId}/{categoryId}")
    public BaseResponse getSellerProductsByCategory(@Valid @PathVariable Long branchId, @PathVariable Long categoryId) {
        Long sellerId = CurrentUserContext.getReferenceId();
        try {
            return baseUtils.generateSuccessResponse(productService.getProductsByBranchAndCategory(branchId,categoryId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }

    }

    @GetMapping("/low-stock/{branchId}")
    public BaseResponse getLowStockProducts(@PathVariable Long branchId,
                                             @RequestParam(value = "threshold", required = false) Integer threshold) {
        try {
            return baseUtils.generateSuccessResponse(productService.getLowStockProductsByBranch(branchId, threshold), "Low-stock inventory retrieved", "কম স্টক ইনভেন্টরি পাওয়া গেছে");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

}
