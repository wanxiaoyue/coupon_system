package com.wanxiaoyuan.coupon.service;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.CouponCategory;
import com.wanxiaoyuan.coupon.constant.DistributeTarget;
import com.wanxiaoyuan.coupon.constant.PeriodType;
import com.wanxiaoyuan.coupon.constant.ProductLine;
import com.wanxiaoyuan.coupon.vo.TemplateRequest;
import com.wanxiaoyuan.coupon.vo.TemplateRule;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.ApplicationContextTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * <h1>构造优惠券模板服务测试</h1>
 * Created by WanYue
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest extends ApplicationContextTestUtils {

    @Autowired
    private ITest test;
    @Test
    public void ITest(){
        test.test();
    }


    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception{

        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        Thread.sleep(5000); //Test只是跑一次，异步服务可能还没有跑完，所以需要跑完才行，否则要异常
    }

    /**
     * <h2>fake TemplateRequest</h2>
     */
    private TemplateRequest fakeTemplateRequest(){

        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("http://wwww.wanxiaoyuan.com");
        request.setDesc("这是一张优惠券模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10000);
        request.setUserId(100001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "重庆市", "重庆市",
                JSON.toJSONString(Arrays.asList("文娱", "家居"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
