package com.company.efood.seller.services.servicesimpl;

import com.company.efood.base.BaseDropdownModel;
import com.company.efood.base.BasePageableRequest;
import com.company.efood.seller.dto.OpeningHourDto;
import com.company.efood.seller.services.OpeningHourService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OpeningHourServiceImpl implements OpeningHourService {
    @Override
    public OpeningHourDto save(OpeningHourDto obj, Long userId) {
        return null;
    }

    @Override
    public OpeningHourDto update(OpeningHourDto obj, Long userId) {
        return null;
    }

    @Override
    public boolean delete(OpeningHourDto obj, Long userId) {
        return false;
    }

    @Override
    public OpeningHourDto getById(Long id, Long userId) {
        return null;
    }

    @Override
    public List<BaseDropdownModel> getDropdownList(Long userId) {
        return List.of();
    }

    @Override
    public Page<OpeningHourDto> getPageableAllData(BasePageableRequest pageableBodyRequest, Long userId) {
        return null;
    }
}
