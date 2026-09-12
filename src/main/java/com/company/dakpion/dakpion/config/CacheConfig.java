package com.company.dakpion.dakpion.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of(
                "dakpion_themes",
                "dakpion_audio_tracks",
                "dakpion_delivery_options",
                "dakpion_pricing_plans",
                "dakpion_testimonials",
                "dakpion_faq"
        ));
        return cacheManager;
    }
}
