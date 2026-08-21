package com.company.efood.sys.services;

import com.company.efood.base.BasePageableRequest;
import com.company.efood.base.BaseService;
import com.company.efood.sys.dto.ProductDto;
import com.company.efood.sys.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService extends BaseService<ProductDto> {

    Page<ProductDto> getPaginatedProductsByBranch(BasePageableRequest basePageableRequest,Long branchId );
    List<ProductDto> getProductsByBranchAndCategory(Long branchId,Long categoryId);
    Page<ProductDto> getPageableProductByBranch(BasePageableRequest basePageableRequest,Long branchId);
    List<ProductDto> getLowStockProductsByBranch(Long branchId, Integer threshold);
    Page<ProductDto> getProductsWithFilters(Long branchId, Long categoryId, String productType, String serviceType, String search, int page, int size);
}
