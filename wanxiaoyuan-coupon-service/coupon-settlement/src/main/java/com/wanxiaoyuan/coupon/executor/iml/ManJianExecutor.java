package com.wanxiaoyuan.coupon.executor.iml;

import com.wanxiaoyuan.coupon.constant.RuleFlag;
import com.wanxiaoyuan.coupon.executor.AbstractExecutor;
import com.wanxiaoyuan.coupon.executor.RuleExecutor;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * <h1>满减优惠券结算规则执行器</h1>
 * Created by WanYue
 */

@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {


    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * <h2>优惠券规则的计算</h2>
     *
     * @param settlement {@link SettlementInfo}  包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息， 会对传入的Info进行校验，如果有错误，会修正
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos())
        );
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if(null != probability){
            log.debug("MainJian Template Is Not Match To GoodTypes");
            return probability;
        }

        //判断满减是否符合减免标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double base = (double)templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        //如果不符合标准，则直接返回商品总价
        if(goodsSum < base){
            log.debug("Current Goods Cost Sum < ManJian Coupon Base");
            settlement.setCost(goodsSum);
            //如果不符合就将优惠券新置空，因为前端既然你没有可用的，就不用显示了
            settlement.setCouponAndTemplateInfos(Collections.EMPTY_LIST);
            return settlement;
        }

        // 计算使用优惠券之后的价格,还要考虑最小值，可能满减后是负数
        settlement.setCost(retain2Decimals(
                (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()
        ));
        log.debug("Use ManJian Coupon Manke Goods Cost From {} To {}", goodsSum,settlement.getCost());
        return settlement;
    }
}
