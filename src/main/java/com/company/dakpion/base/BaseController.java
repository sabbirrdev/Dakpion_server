package com.company.dakpion.base;

import jakarta.servlet.http.HttpServletRequest;

public interface BaseController<B> {
    BaseResponse save(B body, HttpServletRequest request);

    BaseResponse update(B body, HttpServletRequest request);

    BaseResponse delete(B body, HttpServletRequest request);

    BaseResponse getById(Long id, HttpServletRequest request);

    BaseResponse getDropdownList(HttpServletRequest request);
    BaseResponse getAllPageableData(BasePageableRequest basePageableRequest, HttpServletRequest request);
}
