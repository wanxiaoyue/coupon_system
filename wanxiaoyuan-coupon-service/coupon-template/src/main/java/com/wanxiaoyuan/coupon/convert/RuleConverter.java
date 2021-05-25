package com.wanxiaoyuan.coupon.convert;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by WanYue
 */
@Converter
public class RuleConverter
        implements AttributeConverter<TemplateRule, String> {

    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String rule) {
        return JSON.parseObject(rule, TemplateRule.class);
    }
}
