package com.wanxiaoyuan.coupon.service;

import com.wanxiaoyuan.coupon.entity.CouponTemplate;

/**
 * <h1>异步服务接口定义</h1>
 * Created by WanYue
 */

public interface IAsyncService {

    //异步直接返回，不会造成系统耗时情况
    /**
     * <h2>根据模板异步的创建优惠券码</h2>
     * @param  template {@link CouponTemplate} 优惠券模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
