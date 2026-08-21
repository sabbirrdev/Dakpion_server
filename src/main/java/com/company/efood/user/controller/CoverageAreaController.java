package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.entity.CoverageArea;
import com.company.efood.sys.repository.CoverageAreaRepo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;
import static com.company.efood.base.BaseConstants.PUBLIC_ENDPOINT;

@RestController
@AllArgsConstructor
public class CoverageAreaController {

    private final CoverageAreaRepo coverageAreaRepo;
    private final BaseUtils baseUtils;

    @GetMapping(PUBLIC_ENDPOINT + "coverage-area/all")
    public BaseResponse getAllActiveCoverageAreas() {
        try {
            List<CoverageArea> list = coverageAreaRepo.findByActiveTrue();
            return baseUtils.generateSuccessResponse(list, "Coverage areas fetched successfully", "কভারেজ এরিয়া তালিকা প্রদান করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping(PRIVET_ENDPOINT + "coverage-area/save")
    public BaseResponse saveCoverageArea(@RequestBody CoverageArea coverageArea) {
        try {
            if (coverageArea.getDeliveryCharge() == null) {
                coverageArea.setDeliveryCharge(10.0);
            }
            coverageArea.setActive(true);
            CoverageArea saved = coverageAreaRepo.save(coverageArea);
            return baseUtils.generateSuccessResponse(saved, "Coverage area saved", "কভারেজ এরিয়া সংরক্ষণ করা হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
