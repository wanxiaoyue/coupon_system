package com.wanxiaoyuan.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * <h1>通用的抽象过滤器类</h1>
 * Created by WanYue
 */

public abstract class AbstractZuulFilter extends ZuulFilter {

    //用于在过滤器之间传递消息，数据保存再每个请求的ThreadLocal中,请求和相应都放在这里面
    //拓展了Map接口，实际上是concurrentHashMap,  k-v
    RequestContext context;

//    用于标识之后怎么走
    private  final static String NEXT = "next";


    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (Boolean) ctx.getOrDefault(NEXT, true);
    }

    @Override
    public Object run() throws ZuulException {

        context = RequestContext.getCurrentContext();

        return cRun(); //去执行自己的过滤器
    }

    protected  abstract Object cRun();

    Object fail(int code, String msg){
        context.set(NEXT, false);
        context.setSendZuulResponse(false);//直接中断用我们的返回，不用服务器返回
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format("{\"result\":\"%s!\"}",msg));

        return null;
    }

    Object success(){

        context.set(NEXT, true);

        return null;
    }
}
