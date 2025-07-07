package com.eq.rediscache2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * create time 2025/7/6 21:22
 * 文件说明
 *
 * @author xuejiaming
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan("com.eq.rediscache2.*")
public class CacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }
}
