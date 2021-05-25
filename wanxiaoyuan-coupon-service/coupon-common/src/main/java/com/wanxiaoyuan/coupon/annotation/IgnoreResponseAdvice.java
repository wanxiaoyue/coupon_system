package com.wanxiaoyuan.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>忽略统一响应注解定义</h1>
 * Created by WanYue
 */
@Target({ElementType.TYPE, ElementType.METHOD})//定义在类和方法上起作用
@Retention(RetentionPolicy.RUNTIME)//在运行的时候起作用
public @interface IgnoreResponseAdvice {

}
