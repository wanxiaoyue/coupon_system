package com.wanxiaoyuan.coupon.service;

import com.wanxiaoyuan.coupon.entity.Coupon;
import com.wanxiaoyuan.coupon.exception.CouponException;

import java.util.List;

/**
 * <h1>redis相关的操作服务接口定义</h1>
 * 1. 用户的三个状态优惠券 Cache 相关操作
 * 2. 优惠券模板生成的优惠券 Cache 操作
 * Created by WanYue
 */

public interface IRedisService {

    /**
     * <h2>根据 userId 和状态找到缓存的优惠券列表数据</h2>
     * @param userId 用户 id
     * @param status 优惠券状态 {@link com.wanxiaoyuan.coupon.constant.CouponStatus}
     * @return {@link Coupon} 注意，可能会返回 Null, 代表从来没有记录过
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);


    /**
     *<h2>保存空的优惠券列表到缓存中</h2> 避免缓存穿透，防止去数据库查找
     * @param userId 用户 id
     * @param status 优惠券状态列表
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * <h2>尝试从 Cache 中获取一个优惠券码</h2>
     * @param templateId 优惠券模板主键
     * @return 优惠券码， 也可能为Null
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * <h2>将优惠券保存到 Cache 中</h2>
     * @param userId 用户 Id
     * @param coupons {@link Coupon}s
     * @param status 优惠券状态，只有没使用的才行。
     * @return 保存成功的个数
     * @throws CouponException
     */
   Integer addCouponToCache(Long userId, List<Coupon> coupons,
                            Integer status) throws CouponException;
}
