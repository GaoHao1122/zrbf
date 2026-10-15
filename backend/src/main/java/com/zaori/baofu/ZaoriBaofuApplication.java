package com.zaori.baofu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 《早日暴富》小程序后端服务
 * 技术栈：Spring Boot 3 + MyBatis-Plus + MySQL + Redis
 */
@SpringBootApplication
@MapperScan("com.zaori.baofu.mapper")
@EnableScheduling
public class ZaoriBaofuApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZaoriBaofuApplication.class, args);
    }
}
