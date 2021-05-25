package com.wanxiaoyuan.coupon.service;

import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.vo.TemplateRequest;

/**
 * <h1>构建优惠券模板定义</h1>
 * Created by WanYue
 */

public interface IBuildTemplateService {

    /**
     * <h2>创建优惠券模板</h2>
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
   CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
