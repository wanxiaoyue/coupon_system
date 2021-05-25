package com.wanxiaoyuan.coupon.Controller;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.executor.ExecuteManager;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>结算服务 Controller</h1>
 * Created by WanYue
 */

@Slf4j
@RestController
public class SettlementController {

    /** 结算规则执行管理器 */
    @Autowired
    private final ExecuteManager executeManager;

    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * <h2>优惠券结算</h2>
     * 127.0.0.1:7003/coupon-settlement//settlement/compute
     * 127.0.0.1:9000/wanxiaoyuan/coupon-settlement//settlement/compute
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)throws CouponException {
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
