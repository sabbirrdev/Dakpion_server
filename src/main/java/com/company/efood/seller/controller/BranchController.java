package com.company.efood.seller.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.seller.dto.BranchDto;
import com.company.efood.seller.services.BranchService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@RestController()
@RequestMapping(SELLER_END_POINT+SHOP_END_POINT+BRANCH_END_POINT)
@AllArgsConstructor
public class BranchController implements BaseController<BranchDto> {

    private BranchService branchService;
    private BaseUtils baseUtils;
    private AuthTokenUtils authTokenUtils;



    @Override
    @PostMapping
    public BaseResponse save(@Valid  @RequestBody BranchDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@Valid @RequestBody BranchDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@RequestBody BranchDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.delete(body, authTokenUtils.getUserIdFromRequest(request)), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(GET_OBJECT_BY_ID)
    public BaseResponse getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.getById(id, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    public BaseResponse getDropdownList(HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.getDropdownList(authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = DROPDOWN_LIST_PATH)
    public BaseResponse getDropdownListByShop(@RequestParam(value = "shopId") Long shopId, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(branchService.getDropdownListByShopId(shopId,authTokenUtils.getUserIdFromRequest(request)),PROCESS_COMPLETE,PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@RequestBody @Valid BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return  baseUtils.generateSuccessResponse(branchService.getPageableAllData(basePageableRequest,authTokenUtils.getUserIdFromRequest(request)));
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }
}
