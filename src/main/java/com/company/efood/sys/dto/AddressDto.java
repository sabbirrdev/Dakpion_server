package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import com.company.efood.sys.utils.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto extends BaseDto {
    private AddressType addressType;
    private String district;
    private String policeStation;
    private String postOffice;
    private Double lat;
    private Double lon;
    private Number postCode;
    private String houseNo;
    private Number roadNo;
    private String address;
    private String label; // e.g. "Home", "Office", "Mom's House"
    
    // Zone hierarchy links
    private Long zoneId;
    private String zoneName;
    private Long upazilaId;
    private String upazilaName;
    private Long districtId;
    private String districtName;
    private Long divisionId;
    private String divisionName;
}
