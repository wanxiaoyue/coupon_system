package com.wanxiaoyuan.coupon.executor.iml;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.CouponCategory;
import com.wanxiaoyuan.coupon.constant.RuleFlag;
import com.wanxiaoyuan.coupon.executor.AbstractExecutor;
import com.wanxiaoyuan.coupon.executor.RuleExecutor;
import com.wanxiaoyuan.coupon.vo.GoodsInfo;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>满减 + 折扣优惠券结算规则执行器</h1>
 * Created by WanYue
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor
        implements RuleExecutor {

    /**
     * <h2>规则类型标记</h2>
     *
     * @return {@link RuleFlag}
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>检验商品类型与优惠券是否匹配</h2>
     * 需要注意：
     * 1. 这里实现的满减+折扣品类优惠券的校验， 多品类优惠券重载此方法
     * 2. 如果想要使用多累优惠券， 则必须要所有的商品类型都包含在内，即差集为空, A - B,为空， 说明 B包含了所有的A
     * @param settlement {@link SettlementInfo} 用户传递的计算信息
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {

        log.debug("Check ManJian And ZheKou Is Match Or Not!");
        List<Integer> goodType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settlement.getCouponAndTemplateInfos().forEach(ct -> {

            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));

        });

        //如果想要使用多累优惠券， 则必须要所有的商品类型都包含在内，即差集为空

        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodType, templateGoodsType));
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
        //商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum
        );
        if (null != probability){
            log.debug("ManJian And ZheKou Template Is No Match To GoodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian = null;
        SettlementInfo.CouponAndTemplateInfo zheKou = null;

        for (SettlementInfo.CouponAndTemplateInfo ct
                : settlement.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) ==
            CouponCategory.MANJIAN){
                manJian = ct;
            }else {
                zheKou = ct;
            }
        }

        assert null != manJian;
        assert null != zheKou;

        //当前的折扣优惠券和满减券如果不能共用(一起使用)， 清空优惠券，返回商品原价
        if(!isTemplateCanShared(manJian, zheKou)){
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }

        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule()
                .getDiscount().getQuota();

        //最终的价格
        //先计算满减
        double targetSum = goodsSum;
        if(targetSum >= manJianBase) {
            targetSum -= manJianQuota;
            ctInfos.add(manJian);
        }

        //再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule()
                .getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zheKou);

        settlement.setCouponAndTemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(
                targetSum > minCost() ?
                        targetSum:minCost()
        ));
        log.debug("Use ManJian And Zhekou Coupon Make Goods Cost From {} To {} ",
                goodsSum, settlement.getCost());

        return settlement;

    }

    /**
     * <h2>当前的两张优惠券是否可以共用</h2>
     * 即校验 TemplateRule 中的 weight 是否满足条件
     */
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manJian,
                                        SettlementInfo.CouponAndTemplateInfo zheKou){
        String manjianKey = manJian.getTemplate().getKey()
                +String.format("%04d", manJian.getTemplate().getId());
        String zhekouKey = zheKou.getTemplate().getKey()
                + String.format("%04d", zheKou.getTemplate().getId());

        // 对于满减模板可以和那些优惠券模板一起使用，1.首先是自己  2.在Weight里的也可以
        List<String> allSharedKeysForManjian = new ArrayList<>();
        allSharedKeysForManjian.add(manjianKey);
        allSharedKeysForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));
        List<String> allSharedKeysForZhoukou = new ArrayList<>();
        allSharedKeysForZhoukou.add(zhekouKey);
        allSharedKeysForZhoukou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));
        //想要满减和折扣一起使用，前者是后者的子集，那么就可以一起使用了
        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey,zhekouKey), allSharedKeysForManjian)
                || CollectionUtils.isSubCollection(
                        Arrays.asList(manjianKey,zhekouKey), allSharedKeysForZhoukou) ;
    }
}
