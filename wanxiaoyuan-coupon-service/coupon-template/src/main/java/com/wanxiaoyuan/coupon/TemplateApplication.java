package com.wanxiaoyuan.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by WanYue
 */
@EnableScheduling
@EnableJpaAuditing
@EnableEurekaClient
@SpringBootApplication
public class TemplateApplication {

    public static void main(String[] args){
        SpringApplication.run(TemplateApplication.class, args);
    }
}

