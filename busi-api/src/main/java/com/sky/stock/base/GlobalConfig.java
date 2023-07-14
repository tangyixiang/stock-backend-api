package com.sky.stock.base;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

public class GlobalConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(8, TimeUnit.HOURS);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;

    }

}
