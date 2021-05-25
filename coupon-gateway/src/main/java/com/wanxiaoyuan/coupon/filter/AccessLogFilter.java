package com.wanxiaoyuan.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by WanYue
 */
@Slf4j
@Component
public class AccessLogFilter extends  AbstractPostZuulFilter{
    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();

        //从PreRequestFilter中获取请求的时间戳
        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        long duration = System.currentTimeMillis() - startTime;

        //从网关通过的请求都会打印日志记录：uri +  duration
        log.info("uri: {}, duration: {}", uri, duration);

        return success();

    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}