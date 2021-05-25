package com.wanxiaoyuan.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * <h1>分发微服务的启动入口</h1>
 * Created by WanYue
 */
@EnableJpaAuditing   //JPA的审计含义
@EnableFeignClients    //均衡负载请求
@EnableCircuitBreaker  //熔断降级，断路器
@EnableEurekaClient
@SpringBootApplication
public class DistributionApplication {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {

        SpringApplication.run(DistributionApplication.class, args);
    }
}
