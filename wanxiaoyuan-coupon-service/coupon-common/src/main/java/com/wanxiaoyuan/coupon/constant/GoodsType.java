package com.wanxiaoyuan.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by WanYue
 */
@Getter
@AllArgsConstructor
public enum GoodsType {

    WENYU("文娱", 1),
    SHENGXIAN("生鲜", 2),
    JIAJV("家具", 3),
    OTHERS("其他", 4),
    ALL("全品类", 5);

    /** 商品类型描述 */
    private String description;

    /** 商品类型编码 */
    private Integer code;

    public static GoodsType of(Integer code){

        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + "not exists")
                );
    }
}
