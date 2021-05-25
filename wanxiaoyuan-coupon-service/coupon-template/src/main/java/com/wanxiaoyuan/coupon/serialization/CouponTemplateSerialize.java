package com.wanxiaoyuan.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <h1>优惠券模板实体类自定义序列化器</h1>
 * Created by WanYue
 */

public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {

    @Override
    public void serialize(CouponTemplate couponTemplate, //想要去序列化对象
                          JsonGenerator jsonGenerator, //生成json器
                          SerializerProvider serializerProvider) throws IOException {
        //开始序列化对象
        jsonGenerator.writeStartObject();
        //这样好处的就是直接在返回的时候用Json就处理，就不要你额外用业务代码去处理
        jsonGenerator.writeStringField("id", couponTemplate.getId().toString());
        jsonGenerator.writeStringField("name", couponTemplate.getName());
        jsonGenerator.writeStringField("logo", couponTemplate.getLogo());
        jsonGenerator.writeStringField("desc", couponTemplate.getDesc());
        jsonGenerator.writeStringField("category", couponTemplate.getCategory().getDescription());//直接给客户看描述信息即可
        jsonGenerator.writeStringField("productLine", couponTemplate.getProductLine().getDescription());
        jsonGenerator.writeStringField("count", couponTemplate.getCount().toString());
        jsonGenerator.writeStringField("createTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        jsonGenerator.writeStringField("userId", couponTemplate.getUserId().toString());
        jsonGenerator.writeStringField("key",
                couponTemplate.getKey() + String.format("%04d", couponTemplate.getId()));
        jsonGenerator.writeStringField("target", couponTemplate.getTarget().getDescription());
        jsonGenerator.writeStringField("rule", JSON.toJSONString(couponTemplate.getRule()));

        // 结束序列化对象
        jsonGenerator.writeEndObject();
    }
}
