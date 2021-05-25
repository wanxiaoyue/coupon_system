package com.wanxiaoyuan.coupon.feign;

import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.feign.hystrix.SettlementClientHystrix;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <h1>优惠券结算微服务 Feign 接口定义</h1>
 * Created by WanYue
 */
@FeignClient(value = "eureka-client-coupon-settlement", fallback = SettlementClientHystrix.class)
public interface SettlementClient {
    /**
     * <h2>优惠券规则计算</h2>
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute", method = RequestMethod.POST)
    CommonResponse<SettlementInfo> couponRule(@RequestBody SettlementInfo settlementInfo)
        throws CouponException;
}
