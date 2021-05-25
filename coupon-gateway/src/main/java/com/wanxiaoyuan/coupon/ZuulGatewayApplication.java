package com.wanxiaoyuan.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <h1>网关应用启动入口</h1>
 * 1.@EnableZuulProxy 标识当前的应用是 Zuul Server
 * 2.@SpringCloudApplication 组合了springbootapplication、服务发现、熔断
 * Created by WanYue
 */
@EnableZuulProxy
@EnableFeignClients       //一定需要加入， 因为 Permission-sdk 中写了 Feign 接口
@SpringCloudApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}
