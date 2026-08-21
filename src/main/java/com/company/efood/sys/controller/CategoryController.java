package com.company.efood.sys.controller;

import com.company.efood.base.BaseController;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.dto.CategoryDto;
import com.company.efood.sys.services.CategoryService;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.company.efood.base.BaseConstants.*;

@AllArgsConstructor
@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + CATEGORY_END_POINT)
public class CategoryController implements BaseController<CategoryDto> {

    private final BaseUtils baseUtils;
    private final CategoryService categoryService;
    private final AuthTokenUtils authTokenUtils;

    @Override
    @PostMapping
    public BaseResponse save(@RequestBody @Valid CategoryDto body, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.save(body, userId), SAVE_MESSAGE, SAVE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping
    public BaseResponse update(@RequestBody @Valid CategoryDto body, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.update(body, userId), UPDATE_MESSAGE, UPDATE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @DeleteMapping
    public BaseResponse delete(@RequestBody CategoryDto body, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.delete(body, userId), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            CategoryDto dto = new CategoryDto();
            dto.setId(id);
            return baseUtils.generateSuccessResponse(categoryService.delete(dto, userId), DELETE_MESSAGE, DELETE_MESSAGE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(GET_OBJECT_BY_ID)
    public BaseResponse getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.getById(id, userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @GetMapping(value = DROPDOWN_LIST_PATH)
    public BaseResponse getDropdownList(HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.getDropdownList(userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping(value = SUB_CATEGORY + DROPDOWN_LIST_PATH)
    public BaseResponse getSubcategoryDropdownList(@RequestParam("parentId") Long parentId, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.getDropdownListByParentId(parentId, userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @Override
    @PutMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableData(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.getPageableAllData(basePageableRequest, userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PostMapping(value = PAGEABLE_DATA_PATH)
    public BaseResponse getAllPageableDataPost(@Valid @RequestBody BasePageableRequest basePageableRequest, HttpServletRequest request) {
        try {
            Long userId = getUserIdSafe(request);
            return baseUtils.generateSuccessResponse(categoryService.getPageableAllData(basePageableRequest, userId), PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    private Long getUserIdSafe(HttpServletRequest request) {
        try {
            return authTokenUtils.getUserIdFromRequest(request);
        } catch (Exception e) {
            return 0L;
        }
    }
}
