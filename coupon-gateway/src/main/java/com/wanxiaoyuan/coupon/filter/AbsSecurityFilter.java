package com.wanxiaoyuan.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h1>抽象权限过滤器， 可以有多种实现</h1>
 * Created by WanYue
 */
@Slf4j
public abstract class AbsSecurityFilter extends ZuulFilter {

    @Override
    public String filterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {

        //获取到当前上下文环境
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getResponse();

        //如果前一个 filter 执行失败，不会调用后面的 filter

        return response.getStatus() == 0
                || response.getStatus() == HttpStatus.SC_OK;

    }

    //执行的逻辑在run方法中定义
    @Override
    public Object run() throws ZuulException {

        //为了拿到request和repose
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        log.info("filter {} begin check request {}.",
                this.getClass().getSimpleName(), request.getRequestURI());

        Boolean result = null;
        try{
            result = interceptCheck(request, response);
        }catch (Exception e){
            log.error("filter {} check request {}, throws exception {}.",
                    this.getClass().getSimpleName(),
                    request.getRequestURI(), e.getMessage());
        }

        log.info("filter {} finish check, result {}",
                this.getClass().getSimpleName(), result);

        if(result == null){
            log.debug("Filter {} finish check, result is null.",
                    this.getClass().getSimpleName());

            //对当前的请求不进行路由
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(getHttpStatus());
            return null;
        }

        if(!result){

            try{
                //对当前的请求不进行路由
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(getHttpStatus());
                response.setHeader("Content-type",
                        "application//json;charset=UTF-8"); //这样设置为json 是为了后面getErrorMsg是一个可序列化的对象
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(getErrorMsg());
                context.setResponse(response);
            }catch (IOException ex){
                log.error("filter {} check request {}, result is false," +
                        "setResponse throws Exception {}",
                        this.getClass().getSimpleName(),
                        request.getRequestURI(),ex.getMessage());
            }

        }
        //说明请求正确直接返回即可
        return null;
    }

    /**
     * <h2>子 Filter 实现该方法，填充校验逻辑</h2>
     * @return true: 通过校验； false: 校验未通过
     */
    protected abstract Boolean interceptCheck(HttpServletRequest request,
                                              HttpServletResponse response)
        throws Exception;

    protected abstract int getHttpStatus();

    protected abstract String getErrorMsg();
}
