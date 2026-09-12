package com.company.dakpion.dakpion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PricingPlanDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalizedTextDto name;
    private LocalizedTextDto tagline;
    private Double price;
    private LocalizedTextDto billingUnit;
    private List<LocalizedTextDto> features;
    private Boolean highlighted;
}
