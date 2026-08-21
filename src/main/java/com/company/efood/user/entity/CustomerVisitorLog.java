package com.company.efood.user.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "CUSTOMER_VISITOR_LOG")
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerVisitorLog extends BaseEntity {

    @Column(name = "VISITOR_UUID", length = 100, nullable = false)
    private String visitorUuid;

    @Column(name = "IP_ADDRESS", length = 50)
    private String ipAddress;

    @Column(name = "APP_TYPE", length = 50)
    private String appType; // "CUSTOMER_APP", "WEB_CUSTOMER", "PORTAL"

    @Column(name = "USER_AGENT", length = 255)
    private String userAgent;
}
