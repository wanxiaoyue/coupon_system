package com.wanxiaoyuan.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.entity.Coupon;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.service.IUserService;
import com.wanxiaoyuan.coupon.vo.AcquireTemplateRequest;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>用户服务 Controller</h1>
 * Created by WanYue
 */

@Slf4j
@RestController
public class UserServiceController {

    //用户服务接口
    private final IUserService userService;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }


    /**
     * <h2>根据用户 id 和优惠券状态查找用户优惠券记录</h2>
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam("userId") Long userId,@RequestParam("status") Integer status
    ) throws CouponException{
        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponsByStatus(userId, status);
    }

    /**
     * <h2>根据用户 id 查找当前可以领取的优惠券模板 </h2>
     */
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam("userId") Long userId) throws CouponException{

        log.info("Find Available Template: {}", userId);

        return userService.findAvailableTemplate(userId);
    }

    /**
     * <h2>用户领取优惠券</h2>
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException{

        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * <h2>结算与核销优惠券</h2>
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException{

        log.info("Settlement: {}", JSON.toJSONString(info));

        return userService.settlement(info);
    }
}
