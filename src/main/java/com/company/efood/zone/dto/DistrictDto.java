package com.company.efood.zone.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DistrictDto {
    private Long id;
    private String name;
    private String nameBn;
    private Boolean active = true;
    private Boolean isActive = true;
    private Long divisionId;
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
