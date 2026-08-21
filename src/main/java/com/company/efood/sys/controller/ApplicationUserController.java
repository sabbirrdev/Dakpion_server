package com.company.efood.sys.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.AppUserDto;
import com.company.efood.sys.services.ApplicationUserService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;


@AllArgsConstructor
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "app-user")
public class ApplicationUserController implements BaseController<AppUserDto> {

    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;
    private final ApplicationUserService appUserService;

    @Override
    @PostMapping
    public BaseResponse save(@Valid @RequestBody AppUserDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(appUserService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@Valid @RequestBody AppUserDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(appUserService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@Valid @RequestBody AppUserDto body, HttpServletRequest request) {
        try {
            if (appUserService.delete(body, authTokenUtils.getUserIdFromRequest(request))) {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE, DELETE_MESSAGE_BN);
            } else {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE_FAILED, DELETE_MESSAGE_FAILED_BN);
            }

        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(value = GET_OBJECT_BY_ID, produces = EXTERNAL_MEDIA_TYPE)
    public BaseResponse getById(@PathVariable(OBJECT_ID) Long id, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(appUserService.getById(id, authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    public BaseResponse getDropdownList(HttpServletRequest request) {
        return null;
    }

    @Override
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return  baseUtils.generateSuccessResponse(appUserService.getPageableAllData(basePageableRequest,authTokenUtils.getUserIdFromRequest(request)));
        }catch (Exception ex){
            return  baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping(value = "my-profile")
    public BaseResponse getUserByUserId(HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(appUserService.getUserByUserId(authTokenUtils.getUserIdFromRequest(request)), PROCESS_COMPLETE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }


}
