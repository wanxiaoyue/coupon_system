package com.wanxiaoyuan.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>产品线枚举</h1> 产品线不同，可能优惠券模板不一样，都用一套系统管理不同产品线的优惠券模板
 * Created by WanYue
 */
@Getter
@AllArgsConstructor
public enum ProductLine {

    DAMAO("大猫", 1),
    DABAO("大宝", 2);

    /** 产品线描述*/
    private String description;

    /**产品线编码*/
    private Integer code;

    public static ProductLine of(Integer code){
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists!"));
    }

}
