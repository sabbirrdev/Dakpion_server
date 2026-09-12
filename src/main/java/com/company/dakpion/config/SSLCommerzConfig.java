package com.company.dakpion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sslcommerz")
@Data
public class SSLCommerzConfig {
    private String storeId;
    private String storePassword;
    private String initUrl;
    private String successUrl;
    private String failUrl;
    private String cancelUrl;
}

