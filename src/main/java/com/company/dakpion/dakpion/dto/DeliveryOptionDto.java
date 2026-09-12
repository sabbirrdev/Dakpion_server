package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOptionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private DeliveryType type;
    private LocalizedTextDto name;
    private LocalizedTextDto description;
    private Double price;
    private LocalizedTextDto etaLabel;
    private String icon;
}
