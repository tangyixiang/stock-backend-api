package com.sky.stock;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableCaching
@EnableScheduling
@EnableSpringUtil
@SpringBootApplication
public class StockBackendApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockBackendApiApplication.class, args);
    }

}
