package com.wanxiaoyuan.coupon.feign;

import com.wanxiaoyuan.coupon.feign.hystrix.TemplateClientHystrix;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板微服务 Feign 接口定义</h1>
 * Created by WanYue
 */
@FeignClient(value = "eureka-client-coupon-template",fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all", method = RequestMethod.GET)
    CommonResponse <List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * <h2>获取模板ids 到CouponTemplateSDK 映射</h2>
     */
    @RequestMapping(value = "/coupon-template/template/sdk/infos", method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids //get类型，需要加上请求参数
    );

}
