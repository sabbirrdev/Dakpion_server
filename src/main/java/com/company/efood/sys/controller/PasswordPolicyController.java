package com.company.efood.sys.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.PasswordPolicyDto;
import com.company.efood.sys.services.PasswordPolicyService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;


@AllArgsConstructor
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "password-policy")
public class PasswordPolicyController implements BaseController<PasswordPolicyDto> {

    private final BaseUtils baseUtils;
    private final PasswordPolicyService policyService;
    private final AuthTokenUtils authTokenUtils;

    @Override
    @PostMapping
    public BaseResponse save(@Valid @RequestBody PasswordPolicyDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(policyService.save(body, authTokenUtils.getUserIdFromRequest(request)), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@Valid @RequestBody PasswordPolicyDto body, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(policyService.update(body, authTokenUtils.getUserIdFromRequest(request)), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@Valid @RequestBody PasswordPolicyDto body, HttpServletRequest request) {
        try {
            if (policyService.delete(body, authTokenUtils.getUserIdFromRequest(request))) {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE, DELETE_MESSAGE_BN);
            } else {
                return baseUtils.generateSuccessResponse(null, DELETE_MESSAGE_FAILED, DELETE_MESSAGE_FAILED_BN);
            }
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
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
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            return baseUtils.generateSuccessResponse(policyService.getPageableAllData(basePageableRequest,authTokenUtils.getUserIdFromRequest(request)));
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }


}
