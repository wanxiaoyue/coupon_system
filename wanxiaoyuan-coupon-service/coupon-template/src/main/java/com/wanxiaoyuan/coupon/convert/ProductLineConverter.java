package com.wanxiaoyuan.coupon.convert;

import com.wanxiaoyuan.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


/**
 * Created by WanYue
 */

@Converter
public class ProductLineConverter
        implements AttributeConverter<ProductLine, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
