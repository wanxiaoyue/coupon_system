package com.wanxiaoyuan.coupon.dao;

import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Coupon Dao 接口定义 </h1>
 * Created by WanYue
 */

public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * <h2>根据userId + 状态寻找优惠券记录</h2>
     * where userId = ... and status = ...
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

    /**
     * <h2>根据 userId 寻找优惠券记录</h2>
     * */
    List<Coupon> findAllByUserId(Long userId);

}
