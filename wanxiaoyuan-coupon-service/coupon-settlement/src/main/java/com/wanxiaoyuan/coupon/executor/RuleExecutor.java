package com.wanxiaoyuan.coupon.executor;

import com.wanxiaoyuan.coupon.constant.RuleFlag;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;

/**
 * <h1>优惠券模板规则处理器接口定义</h1>
 * Created by WanYue
 */

public interface RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     * @return {@link RuleFlag}
     */
    RuleFlag ruleConfig();

    /**
     * <h2>优惠券规则的计算</h2>
     * @param settlement {@link SettlementInfo}  包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息， 会对传入的Info进行校验，如果有错误，会修正
     */
    SettlementInfo computeRule(SettlementInfo settlement);

}
