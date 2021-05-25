package com.wanxiaoyuan.coupon.feign.hystrix;

import com.wanxiaoyuan.coupon.feign.TemplateClient;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <h1>优惠券模板 Feign 接口的熔断降级策略</h1>
 * Created by WanYue
 */
@Slf4j
@Service
public class TemplateClientHystrix implements TemplateClient {


    /**
     * <h2>查找所有可用的优惠券模板</h2>
     */
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {

        log.info("[eureka-client-coupon-template] findAllUsableTemplate" + "request error");

        //返回一个空的list，兜底策略
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error" ,
                Collections.emptyList()
        );
    }

    /**
     * <h2>获取模板ids 到CouponTemplateSDK 映射</h2>
     *
     * @param ids 优惠券模板 id
     */
    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {

        log.error("[eureka-client-coupon-template] findIds2TemplateSDK" + "request error");

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-template] request error" ,
                new HashMap<>()
        );
    }
}
