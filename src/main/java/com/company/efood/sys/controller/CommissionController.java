package com.company.efood.sys.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.sys.entity.Commission;
import com.company.efood.sys.repository.CommissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.company.efood.base.BaseConstants.SYSTEM_ADMIN_END_POINT;

@RestController
@RequestMapping(SYSTEM_ADMIN_END_POINT + "commission")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionRepo commissionRepo;
    private final BaseUtils baseUtils;

    @GetMapping("/order/{orderId}")
    public BaseResponse getByOrder(@PathVariable Long orderId) {
        try {
            List<Commission> list = commissionRepo.findByOrderId(orderId);
            return baseUtils.generateSuccessResponse(list, "Success", "Success");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @GetMapping("/shop/{shopId}")
    public BaseResponse getByShop(@PathVariable Long shopId) {
        try {
            List<Commission> list = commissionRepo.findByShopId(shopId);
            return baseUtils.generateSuccessResponse(list, "Success", "Success");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }

    @PutMapping("/{id}/settle")
    public BaseResponse settleCommission(@PathVariable Long id) {
        try {
            Commission commission = commissionRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
            commission.setStatus("SETTLED");
            commission.setPaid(true);
            commissionRepo.save(commission);
            return baseUtils.generateSuccessResponse(commission, "Settled successfully", "Settled successfully");
        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
