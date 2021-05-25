package com.wanxiaoyuan.coupon.vo;

/**
 * <h1>微服务之间用的优惠券模板信息定义</h1>
 * Created by WanYue
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 为什么不直接new Template对象。 还要专门开一个类去搞
 * 1.CouponTemplate是PO对象(映射数据库实体表)，不乱定义
 * 2.可能还会拓展一些内容，而且也可能还有一些信息不好给客户，所以中间加一个类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {

    /** 优惠券模板主键*/
    private Integer id;

    /** 优惠券模板名称 */
    private String name;

    /** 优惠券Logo */
    private  String logo;

    /** 优惠券描述 */
    private String desc;

    /** 优惠券分类 */
    private String category;

    /** 产品线 */
    private Integer productLine;

    /** 优惠券模板编码 */
    private String key;

    /** 目标用户 */
    private  Integer target;

    /** 优惠券规则 */
    private TemplateRule rule;
}
