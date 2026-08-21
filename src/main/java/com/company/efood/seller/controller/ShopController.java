package com.company.efood.seller.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.ShopDto;
import com.company.efood.sys.services.ShopService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@AllArgsConstructor
@RestController
@RequestMapping(SELLER_END_POINT + "shop")
public class ShopController implements BaseController<ShopDto> {

    private ShopService shopService;
    private BaseUtils baseUtils;
    private AuthTokenUtils authTokenUtils;


    @Override
    @PostMapping("/registration")
    public BaseResponse save(@RequestBody @Valid ShopDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@RequestBody @Valid ShopDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@RequestBody ShopDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.delete(body, authTokenUtils.getUserIdFromRequest(request)), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(GET_OBJECT_BY_ID)
    public BaseResponse getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.getById(id, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(DROPDOWN_LIST_PATH)
    public BaseResponse getDropdownList(HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.getDropdownList(authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PostMapping(PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(shopService.getPageableAllData(basePageableRequest, authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    /** GET /api/private/seller/shop/my — Returns the shop owned by the logged-in seller. */
    @GetMapping("/my")
    public BaseResponse getMyShop(HttpServletRequest request) {
        try {
            ShopDto shop = shopService.getMyShop(authTokenUtils.getUserIdFromRequest(request));
            if (shop == null) {
                // 204 No Content with success=true so app knows: no shop yet
                return baseUtils.generateSuccessResponse(null, "No shop found", "শপ পাওয়া যায়নি");
            }
            return baseUtils.generateSuccessResponse(shop, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

}
