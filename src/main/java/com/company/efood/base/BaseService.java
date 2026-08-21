package com.company.efood.base;

import com.company.efood.sys.dto.AppUserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseService<M> {
    M save(M obj, Long userId);

    M update(M obj, Long userId);

    boolean delete(M obj, Long userId);

    M getById(Long id, Long userId);
    List<BaseDropdownModel> getDropdownList(Long userId);
    Page<M> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId);

}
