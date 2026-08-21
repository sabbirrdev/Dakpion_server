package com.company.efood.zone.model;

import lombok.Data;

@Data
public class ZoneCalculateFeeRequest {
    private Long zoneId;
    private double lat;
    private double lon;
}
