package com.wanxiaoyuan.coupon.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义过滤器
 * 在请求校验之前
 * Created by WanYue
 */
@Slf4j
//@Component  //如果不要网关，就不开这个
public class TokenFilter extends AbstractPreZuulFilter{

    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));

        Object token = request.getParameter("token");
        if(null == token){
            log.error("error: token is empty");
            return fail(401, "error: token is empty");
        }
        return success();
    }
//定义优先级，数字越小，越先被执行
    @Override
    public int filterOrder() {
        return 1;
    }
}
