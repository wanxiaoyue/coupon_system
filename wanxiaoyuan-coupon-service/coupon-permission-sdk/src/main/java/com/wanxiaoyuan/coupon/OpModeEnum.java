package com.wanxiaoyuan.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>操作模式的枚举定义，可读或可写</h1>
 * Created by WanYue
 */

@Getter
@AllArgsConstructor
public enum OpModeEnum {

    READ("读"),
    WRITE("写");

    private String mode;

}
