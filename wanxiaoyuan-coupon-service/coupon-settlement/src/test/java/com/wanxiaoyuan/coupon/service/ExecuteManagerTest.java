package com.wanxiaoyuan.coupon.service;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.CouponCategory;
import com.wanxiaoyuan.coupon.constant.GoodsType;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.executor.ExecuteManager;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import com.wanxiaoyuan.coupon.vo.GoodsInfo;
import com.wanxiaoyuan.coupon.vo.SettlementInfo;
import com.wanxiaoyuan.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

/**
 * <1>结算规则执行管理器测试用例</1>
 * 对 Executor 的分发与结算逻辑进行测试
 * Created by WanYue
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExecuteManagerTest {

    /** fake 一个 UserId*/
    private Long fakeUserId = 20001L;

    @Autowired
    private ExecuteManager manager;

    @Test
    public void testComputeRule() throws CouponException{

        //  满减优惠券结算测试
//        log.info("ManJian Coupon Executor Test");
//        SettlementInfo manjianInfo = fakeManJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(manjianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

//        // 折扣优惠券结算测试
//        log.info("ZheKou Coupon Executor Test");
//        SettlementInfo zhekouInfo = fakeZheKouCouponSettlement();
//        SettlementInfo result = manager.computeRule(zhekouInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        // 折扣优惠券结算测试
//        log.info("LiJian Coupon Executor Test");
//        SettlementInfo lijianInfo = fakeLiJianCouponSettlement();
//        SettlementInfo result = manager.computeRule(lijianInfo);
//
//        log.info("{}", result.getCost());
//        log.info("{}", result.getCouponAndTemplateInfos().size());
//        log.info("{}", result.getCouponAndTemplateInfos());

        //满减折扣优惠券测试
        log.info("ManJian ZheKou Coupon Executor Test");
        SettlementInfo manjianZheKouInfo = fakeManJianAndZheKouCouponSettlement();

        SettlementInfo result = manager.computeRule(manjianZheKouInfo);

        log.info("{}", result.getCost());
        log.info("{}", result.getCouponAndTemplateInfos().size());
        log.info("{}", result.getCouponAndTemplateInfos());

    }

    /**
     * <h2>fake(mock) 满减优惠券的结算信息</h2>
     */
    private SettlementInfo fakeManJianCouponSettlement(){

        //基本信息
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        //商品信息
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());


        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());


        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        //优惠券信息
        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        //优惠券的ID
        ctInfo.setId(1);

        CouponTemplateSDK couponTemplateSDK = new CouponTemplateSDK();
        //优惠券模板的ID
        couponTemplateSDK.setId(1);
        couponTemplateSDK.setCategory(CouponCategory.MANJIAN.getCode());
        couponTemplateSDK.setKey("100120120801");

        TemplateRule rule =new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(20, 199));
        rule.setUsage(new TemplateRule.Usage("重庆市", "重庆市",
                JSON.toJSONString(Arrays.asList(GoodsType.WENYU.getCode(),GoodsType.JIAJV.getCode()))));
        couponTemplateSDK.setRule(rule);

        ctInfo.setTemplate(couponTemplateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    /**
     * <h2>fake 折扣优惠券的结算信息</h2>
     */
    private SettlementInfo fakeZheKouCouponSettlement(){

        //基本信息
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        //商品信息
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());


        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(2);
        templateSDK.setCategory(CouponCategory.ZHEKOU.getCode());
        templateSDK.setKey("100220190712");

        //设置 TemplateRule,包含了折扣的信息，和那些类能打折
        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(85, 1));
        rule.setUsage(new TemplateRule.Usage("重庆市", "重庆市",
                JSON.toJSONString(Arrays.asList(
                GoodsType.SHENGXIAN.getCode(),
                GoodsType.JIAJV.getCode()
        ))));

        templateSDK.setRule(rule);

        ctInfo.setTemplate(templateSDK);
        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));

        return info;
    }

    /**
     * <h2>fake 立减优惠券的结算信息</h2>
     */
    private SettlementInfo fakeLiJianCouponSettlement(){
        //基本信息
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        //商品信息
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());


        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(5);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());


        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        SettlementInfo.CouponAndTemplateInfo ctInfo =
                new SettlementInfo.CouponAndTemplateInfo();
        ctInfo.setId(1);

        CouponTemplateSDK templateSDK = new CouponTemplateSDK();
        templateSDK.setId(3);
        templateSDK.setCategory(CouponCategory.LIJIAN.getCode());
        templateSDK.setKey("200320190712"); //优惠券状态(1)+类别(3)+日期(8)

        TemplateRule rule = new TemplateRule();
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setUsage(new TemplateRule.Usage("重庆市", "重庆市",
                JSON.toJSONString(Arrays.asList(  GoodsType.WENYU.getCode(),
                        GoodsType.JIAJV.getCode())
                )));//可以使用的范围
        templateSDK.setRule(rule);
        ctInfo.setTemplate(templateSDK);

        info.setCouponAndTemplateInfos(Collections.singletonList(ctInfo));
        return info;
    }

    /**
     * <h2>fake 满减 + 折扣优惠券结算信息</h2>
     */
    private SettlementInfo fakeManJianAndZheKouCouponSettlement(){
        //基本信息
        SettlementInfo info = new SettlementInfo();
        info.setUserId(fakeUserId);
        info.setEmploy(false);
        info.setCost(0.0);

        //商品信息
        GoodsInfo goodsInfo01 = new GoodsInfo();
        goodsInfo01.setCount(2);
        goodsInfo01.setPrice(10.88);
        goodsInfo01.setType(GoodsType.WENYU.getCode());

        GoodsInfo goodsInfo02 = new GoodsInfo();
        goodsInfo02.setCount(10);
        goodsInfo02.setPrice(20.88);
        goodsInfo02.setType(GoodsType.WENYU.getCode());

        info.setGoodsInfos(Arrays.asList(goodsInfo01, goodsInfo02));

        //满减优惠券
        SettlementInfo.CouponAndTemplateInfo manjianInfo =
                new SettlementInfo.CouponAndTemplateInfo();

        manjianInfo.setId(1);

        CouponTemplateSDK manjianTemplate = new CouponTemplateSDK();
        manjianTemplate.setId(1);
        manjianTemplate.setCategory(CouponCategory.MANJIAN.getCode());
        manjianTemplate.setKey("100120190712");

        TemplateRule manjianRule =new TemplateRule();
        manjianRule.setDiscount(new TemplateRule.Discount(20, 199));
        manjianRule.setUsage(new TemplateRule.Usage("重庆市", "重庆",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),GoodsType.JIAJV.getCode()
                ))));
        manjianTemplate.setRule(manjianRule);
        manjianInfo.setTemplate(manjianTemplate);
        manjianRule.setWeight(JSON.toJSONString(Collections.emptyList()));

        //折扣优惠券
        SettlementInfo.CouponAndTemplateInfo zhekouInfo =
                new SettlementInfo.CouponAndTemplateInfo();

        zhekouInfo.setId(1);

        CouponTemplateSDK zhekouTemplate = new CouponTemplateSDK();
        zhekouTemplate.setId(2);
        zhekouTemplate.setCategory(CouponCategory.ZHEKOU.getCode());
        zhekouTemplate.setKey("100220190712");

        TemplateRule zhekouRule = new TemplateRule();
        zhekouRule.setDiscount(new TemplateRule.Discount(85, 1));
        zhekouRule.setUsage(new TemplateRule.Usage("重庆市", "重庆",
                JSON.toJSONString(Arrays.asList(
                        GoodsType.WENYU.getCode(),GoodsType.JIAJV.getCode()
                ))));
        zhekouRule.setWeight(JSON.toJSONString(
                Collections.singletonList("1001201907120001")
        ));
        zhekouTemplate.setRule(zhekouRule);
        zhekouInfo.setTemplate(zhekouTemplate);

        info.setCouponAndTemplateInfos(Arrays.asList(
                manjianInfo, zhekouInfo
        ));

        return info;
    }
}
