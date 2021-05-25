package com.wanxiaoyuan.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限忽略注解： 忽略当前标志的 Controller 接口， 不注册权限 </h1>
 * Created by WanYue
 */
@Target({ElementType.METHOD}) //定义这个注解在类、方法还是变量上生效
@Retention(RetentionPolicy.RUNTIME) //生效的期间是运行的时候
public @interface IgnorePermission {

}
