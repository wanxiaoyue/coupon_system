package com.wanxiaoyuan.coupon.convert;

import com.wanxiaoyuan.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * <h1>优惠券分类枚举属性转换器</h1>
 * AttributeConverter<X, Y>
 *     x:实体属性的类型
 *     Y：数据库字段的类型
 * Created by WanYue
 */

@Convert
public class CouponCategoryConverter
       implements AttributeConverter<CouponCategory, String> {


    /**
     * <h2>将实体属性X转换为Y存储到数据中心</h2>
     * created by WanYue
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {

        return couponCategory.getCode();
    }

/**
 * <h2>将数据库中的字段Y转换为实体属性X中，查询操作时执行的动作</h2>
 * created by WanYue
 */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
