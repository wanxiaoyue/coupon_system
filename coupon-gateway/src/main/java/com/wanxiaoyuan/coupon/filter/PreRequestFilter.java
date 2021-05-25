package com.wanxiaoyuan.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>在过滤器中存储客服端发起请求的时间戳</h1>
 * Created by WanYue
 */
@Component
@Slf4j
public class PreRequestFilter extends AbstractPreZuulFilter{

    @Override
    protected Object cRun() {

        context.set("startTime", System.currentTimeMillis());

        return success();
    }

    @Override
    public int filterOrder() {
        return 0; //0是优先级最高
    }
}
