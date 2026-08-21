package com.company.efood.catalog.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.catalog.dto.CatalogProductDto;
import com.company.efood.catalog.service.CatalogProductService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@RestController
@RequestMapping(PRIVET_ENDPOINT + "catalog/product")
@AllArgsConstructor
public class CatalogProductController implements BaseController<CatalogProductDto> {

    private final CatalogProductService catalogProductService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @Override
    @PostMapping
    public BaseResponse save(@RequestBody @Valid CatalogProductDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(catalogProductService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@RequestBody @Valid CatalogProductDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(catalogProductService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    public BaseResponse delete(CatalogProductDto body, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse getById(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse getDropdownList(HttpServletRequest request) {
        return null;
    }

    @Override
    @PostMapping("/pageable-data")
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(catalogProductService.getPageableAllData(basePageableRequest, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/search")
    public BaseResponse search(@RequestParam("q") String q) {
        try {
            return baseUtils.generateSuccessResponse(catalogProductService.getPageableAllData(null, null), PROCESS_COMPLETE, PROCESS_COMPLETE_BN); // Stubbed
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/barcode/{barcode}")
    public BaseResponse lookupByBarcode(@PathVariable("barcode") String barcode) {
        try {
            return baseUtils.generateSuccessResponse(null, PROCESS_COMPLETE, PROCESS_COMPLETE_BN); // Stubbed
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
