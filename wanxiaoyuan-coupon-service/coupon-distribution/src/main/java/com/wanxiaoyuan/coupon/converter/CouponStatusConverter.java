package com.wanxiaoyuan.coupon.converter;

import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.entity.Coupon;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * <h1>优惠券状态枚举属性转换器</h1>
 * Created by WanYue
 */

@Converter
public class CouponStatusConverter implements
        AttributeConverter<CouponStatus, Integer> {


    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
