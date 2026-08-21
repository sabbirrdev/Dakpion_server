package com.company.efood.zone.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ZoneDto {
    private Long id;
    private String name;
    private String nameBn;
    private BigDecimal baseDeliveryFee;
    private BigDecimal perKmCharge;
    private Double hubLat;
    private Double hubLon;
    private String geoFencePolygon;
    private Boolean active = true;
    private Boolean isActive = true;
    private Boolean isDeliverable = true;
    private Long upazilaId;
    private String upazilaName;
    private String districtName;
    private String divisionName;

    public Boolean getActive() {
        return active != null ? active : isActive;
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        this.isActive = active;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.active = isActive;
    }
}
