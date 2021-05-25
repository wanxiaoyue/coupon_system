package com.wanxiaoyuan.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限描述注解： 定义 Controller 接口的权限</h1>
 * Created by WanYue
 */
@Target({ElementType.METHOD}) //定义这个注解在类、方法还是变量上生效
@Retention(RetentionPolicy.RUNTIME) //生效的期间是运行的时候
public @interface WanxiaoyuanCouponPermission {

    /**
     * <h2>接口描述信息</h2>
     */
    String description() default "";

    /**
     * <h2>此接口是否为只读， 默认是 true</h2>
     */
    boolean readOnly() default true;

    /**
     * <h2>扩展属性</h2>
     * 最好以 JSON 格式去存储
     */
    String extra() default "";
}
