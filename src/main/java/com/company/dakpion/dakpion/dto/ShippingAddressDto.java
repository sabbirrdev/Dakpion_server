package com.company.dakpion.dakpion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingAddressDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fullAddress;
    private String city;
    private String postalCode;
    private String phone;
}
