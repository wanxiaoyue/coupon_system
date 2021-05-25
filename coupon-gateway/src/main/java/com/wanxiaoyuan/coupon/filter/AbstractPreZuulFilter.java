package com.wanxiaoyuan.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * Created by WanYue
 */

public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
