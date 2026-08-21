package com.company.efood.sys.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.MenuItemDto;
import com.company.efood.sys.services.MenuItemService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;


@AllArgsConstructor
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "menu-item")
public class MenuItemController implements BaseController<MenuItemDto> {
    private final BaseUtils baseUtils;
    private final MenuItemService menuItemService;
    private final AuthTokenUtils authTokenUtils;


    @Override
    @PostMapping
    public BaseResponse save(@Valid @RequestBody MenuItemDto body, HttpServletRequest request) {

        try {
            return baseUtils.generateSuccessResponse(menuItemService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@RequestBody MenuItemDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(menuItemService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@Valid @RequestBody MenuItemDto body, HttpServletRequest request) {
        try {
            final boolean isDelete = menuItemService.delete(body, authTokenUtils.getUserIdFromRequest(request));
            if (isDelete) {
                return baseUtils.generateSuccessResponse(isDelete, DELETE_MESSAGE, DELETE_MESSAGE_BN);
            } else {
                throw new RuntimeException(DELETE_MESSAGE_FAILED);
            }

        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(value = GET_OBJECT_BY_ID, produces = EXTERNAL_MEDIA_TYPE)
    public BaseResponse getById(@PathVariable(OBJECT_ID) Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(menuItemService.getById(id, authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(value = DROPDOWN_LIST_PATH, produces = EXTERNAL_MEDIA_TYPE)
    public BaseResponse getDropdownList(HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(menuItemService.getDropdownList(authTokenUtils.getUserIdFromRequest(request)));
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @Override
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(menuItemService.getPageableAllData(basePageableRequest,authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = "menu-by-app-user-id", produces = EXTERNAL_MEDIA_TYPE)
    public BaseResponse getMenuByUserTypeId(HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(menuItemService.getByAppUserId(authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = "dropdown-list-by-menu-type/{menuType}", produces = EXTERNAL_MEDIA_TYPE)
    public BaseResponse getDropdownListByMenuType(@PathVariable("menuType") String menuType, HttpServletRequest request){
        try {
            return baseUtils.generateSuccessResponse(menuItemService.getDropdownListByMenuType(menuType, authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            e.printStackTrace();
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = "get-module-list")
    public BaseResponse getModuleDropdownList(HttpServletRequest request){
        try {
            return baseUtils.generateSuccessResponse(menuItemService.getModuleList(authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

}
