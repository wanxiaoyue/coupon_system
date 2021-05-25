package com.wanxiaoyuan.coupon.service;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>优惠券模板基础服务的测试</h1>
 * Created by WanYue
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {

    @Autowired
    private ITemplateBaseService templateBaseService;

    @Test
    public void testBuildTemplateInfo() throws CouponException{

        System.out.println(JSON.toJSON(
                templateBaseService.buildTemplateInfo(1)
        ));
    }
}
