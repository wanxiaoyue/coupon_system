package com.wanxiaoyuan.coupon.feign.hystrix;

import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.feign.SettlementClient;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <h1>结算微服务熔断策略实现</h1>
 * Created by WanYue
 */

@Slf4j
@Service
public class SettlementClientHystrix implements SettlementClient {
    /**
     * <h2>优惠券规则计算</h2>
     * @param settlementInfo
     */
    @Override
    public CommonResponse<SettlementInfo> couponRule(SettlementInfo settlementInfo) throws CouponException {

        log.info("[eureka-client-coupon-settlement] computeRule" + "request error");

        //表示结算微服务不可用
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error",
                settlementInfo
        );
    }
}
