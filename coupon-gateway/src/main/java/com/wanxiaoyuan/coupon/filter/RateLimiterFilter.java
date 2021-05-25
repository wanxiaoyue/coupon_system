package com.wanxiaoyuan.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>限流过滤器</h1>
 * Created by WanYue
 */
@Slf4j
@Component
@SuppressWarnings("all")//警告信息
public class RateLimiterFilter extends AbstractPreZuulFilter{

    /**每秒可以获取到两个令牌*/
    RateLimiter rateLimiter = RateLimiter.create(2.0);

    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();

        if(rateLimiter.tryAcquire()){
            log.info("get rate token success");
            return success();
        }else{
            log.error("rate limit: {}", request.getRequestURI());
            return fail(402, "error: rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2; //通过了token才能开始限流，根据自己业务逻辑逻辑即可
    }
}