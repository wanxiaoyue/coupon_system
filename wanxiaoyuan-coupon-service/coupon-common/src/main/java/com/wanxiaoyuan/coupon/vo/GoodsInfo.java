package com.wanxiaoyuan.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>fake 商品信息</h1>  因为一般都是看了商品去领券
 * Created by WanYue
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo  {

    /** 商品类型 映射到枚举类*/
    private Integer type;

    /** 商品价格*/
    private Double price;

    /** 商品数量*/
    private Integer count;

    //TODO 名称，使用信息
}
