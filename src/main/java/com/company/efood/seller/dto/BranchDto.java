package com.company.efood.seller.dto;
import com.company.efood.base.BaseDto;
import com.company.efood.sys.dto.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BranchDto extends BaseDto {
    private String name;
    private AddressDto address;
    private Boolean isOpen;
    private Integer shopId;
    private String about;
    private String logoUrl;
    private Long reviewId;
    private Integer reviewCount;
    private BigDecimal rating;
    private List<OpeningHourDto> openingHours;
}
