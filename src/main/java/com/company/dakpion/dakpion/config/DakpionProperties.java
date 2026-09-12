package com.company.dakpion.dakpion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dakpion")
public class DakpionProperties {

    private Security security = new Security();
    private Otp otp = new Otp();
    private Moderation moderation = new Moderation();
    private Sms sms = new Sms();
    private Courier courier = new Courier();

    @Data
    public static class Security {
        private String phonePepper = "dakpion-nostalgic-postbox-pepper-secret-2026";
    }

    @Data
    public static class Otp {
        private long ttlSeconds = 300;
        private RateLimit rateLimit = new RateLimit();

        @Data
        public static class RateLimit {
            private long windowSeconds = 60;
            private int hourlyLimit = 5;
        }
    }

    @Data
    public static class Moderation {
        private boolean autoApproveDigital = false;
    }

    @Data
    public static class Sms {
        private String provider = "noop";
        private String apiKey;
        private String senderId;
        private String apiEndpoint;
    }

    @Data
    public static class Courier {
        private String provider = "noop";
        private String apiKey;
        private String secretKey;
        private String baseUrl;
    }
}
