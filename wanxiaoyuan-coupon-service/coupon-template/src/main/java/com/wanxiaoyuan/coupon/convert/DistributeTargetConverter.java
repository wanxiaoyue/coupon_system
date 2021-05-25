package com.wanxiaoyuan.coupon.convert;

import com.wanxiaoyuan.coupon.constant.DistributeTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by WanYue
 */
@Converter
public class DistributeTargetConverter
        implements AttributeConverter<DistributeTarget, Integer> {
    @Override
    public Integer convertToDatabaseColumn(DistributeTarget distributeTarget) {
        return distributeTarget.getCode();
    }

    @Override
    public DistributeTarget convertToEntityAttribute(Integer code) {
        return DistributeTarget.of(code);
    }
}
