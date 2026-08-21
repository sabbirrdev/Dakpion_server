package com.company.efood.sys.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.model.UserRoleModel;
import com.company.efood.sys.services.UserRoleService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@AllArgsConstructor
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "user-role")
public class UserRoleController implements BaseController<UserRoleModel> {

    private final UserRoleService userRoleService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @Override
    @PostMapping
    public BaseResponse save(@Valid @RequestBody UserRoleModel body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(userRoleService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }

    }

    @Override
    @PutMapping
    public BaseResponse update(@Valid @RequestBody UserRoleModel body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(userRoleService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @DeleteMapping
    @Override
    public BaseResponse delete(@Valid @RequestBody UserRoleModel body, HttpServletRequest request) {
        try {
            if (userRoleService.delete(body, authTokenUtils.getUserIdFromRequest(request))) {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE, DELETE_MESSAGE_BN);
            } else {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE_FAILED, DELETE_MESSAGE_FAILED_BN);
            }
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = GET_OBJECT_BY_ID, produces = EXTERNAL_MEDIA_TYPE)
    @Override
    public BaseResponse getById(@PathVariable(OBJECT_ID) Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(userRoleService.getById(id, authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    public BaseResponse getDropdownList(HttpServletRequest request) {
        return null;
    }

    @PutMapping(value = PAGEABLE_DATA_PATH)
    @Override
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try{
            return  baseUtils.generateSuccessResponse(userRoleService.getPageableAllData(basePageableRequest,authTokenUtils.getUserIdFromRequest(request)));
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }


}
