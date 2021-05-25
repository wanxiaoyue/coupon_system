package com.wanxiaoyuan.coupon.service;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * <h1>用户服务功能测试用例</h1>
 * Created by WanYue
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    /** fake 一个 UserId*/
    private Long fakeUserId = 20001L;

    @Autowired
    private IUserService userService;

    @Test
    public void TestRedis(){
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.opsForHash().putAll("ab", new HashMap<String,String>() {{put("1","2");}});
        redisTemplate.opsForHash().putAll("ab", new HashMap<String,String>() {{put("3","4"); }});
    }


    @Test
    public void testFindCouponByStatus() throws CouponException{

        System.out.println(
                JSON.toJSONString(
                        userService.findCouponsByStatus(
                                fakeUserId,
                                CouponStatus.USABLE.getCode()
                        )
                ));
        System.out.println(
                JSON.toJSONString(
                        userService.findCouponsByStatus(
                                fakeUserId,
                                CouponStatus.USED.getCode()
                        )
                ));
        System.out.println(
                JSON.toJSONString(
                        userService.findCouponsByStatus(
                                fakeUserId,
                                CouponStatus.EXPIRED.getCode()
                        )
                ));
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException{
        System.out.println(
                JSON.toJSONString(
                        userService.findAvailableTemplate(fakeUserId)
                        ));
    }
}
