package com.wanxiaoyuan.coupon.executor.iml;

import com.wanxiaoyuan.coupon.constant.RuleFlag;
import com.wanxiaoyuan.coupon.executor.AbstractExecutor;
import com.wanxiaoyuan.coupon.executor.RuleExecutor;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>折扣优惠券结算规则执行器</h1>
 * Created by WanYue
 */

@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor
{
    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * <h2>优惠券规则的计算</h2>
     *
     * @param settlement {@link SettlementInfo}  包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息， 会对传入的Info进行校验，如果有错误，会修正
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(goodsCostSum(
         settlement.getGoodsInfos()
                ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if(null != probability){
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }

        //折扣优惠券可用直接使用， 没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // 计算使用优惠券之后的价格
        settlement.setCost(
                retain2Decimals((goodsSum * (quota * 1.0) / 100)) > minCost() ?
                        retain2Decimals((goodsSum * (quota * 1.0) / 100))
                        : minCost()
        );
        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlement.getCost());
        return settlement;
    }
}