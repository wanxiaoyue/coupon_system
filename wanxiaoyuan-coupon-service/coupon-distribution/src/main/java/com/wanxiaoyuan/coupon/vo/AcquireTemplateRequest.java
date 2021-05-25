package com.wanxiaoyuan.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>获取优惠券请求定义</h1>
 * Created by WanYue
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {

   /** 用户id */
    private Long userId;

    /** 优惠券模板信息*/
    private CouponTemplateSDK templateSDK;

}
