package com.wanxiaoyuan.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * <h1>用户优惠券状态</h1>
 * Created by WanYue
 */

@Getter
@AllArgsConstructor
public enum CouponStatus {

    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期的(未被使用的)", 3);

    /** 优惠券状态描述信息 */
    private String description;

    /** 优惠券状态编码 */
    private Integer code;

    /**
     * <h2>根据 code 获取到 CouponStatus</h2>
     */
    public static CouponStatus of(Integer code){

        Objects.requireNonNull(code);

        //将当前类的枚举传入，看所有枚举的code是否等于，返回任意一个，没有就抛出异常
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}
