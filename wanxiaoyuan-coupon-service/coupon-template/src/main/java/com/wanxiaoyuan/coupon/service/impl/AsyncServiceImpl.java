package com.wanxiaoyuan.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.wanxiaoyuan.coupon.constant.Constant;
import com.wanxiaoyuan.coupon.dao.CouponTemplateDao;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 * Created by WanYue
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    private final CouponTemplateDao couponTemplateDao;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao couponTemplateDao, StringRedisTemplate redisTemplate) {
        this.couponTemplateDao = couponTemplateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据模板异步的创建优惠券码</h2>
     * @param  template {@link CouponTemplate} 优惠券模板实体
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        //wanxiaoyuan_coupon_template_code_
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("Push CouponCode To Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        //优惠券模板生成，优惠券代码也生成
        template.setAvailable(true);
        couponTemplateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 发送短信或者邮件通知优惠券模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /**
     * <h2>构造优惠券码</h2>
     * 优惠券码（对应每一张优惠券码 ，18位）
     * 前四位：产品线+类型
     * 中间六位：日期的随机（190101）
     * 后八位：0~9的随机数
     * @param template {@link CouponTemplate} 实体类
     * @return Set<String> 与template.count 相同个数的优惠卷码
     */
    private Set<String> buildCouponCode(CouponTemplate template){

        //生成可能比较满，所以来一个定时器看看需要多久
        Stopwatch watch = Stopwatch.createStarted();

        //最开始就给初始量，避免扩容
        Set<String> result = new HashSet<>(template.getCount());

        //前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();

        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());  //当前日期，jpa帮我们填充

        for(int i = 0; i != template.getCount(); ++i){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while(result.size() < template.getCount()){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        watch.stop();
        log.info("Build Coupon Code Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * <h2>构造优惠券码的后14位</h2>
     * @param date 创建优惠券的日期
     * @return 14 位优惠码
     */
    private String buildCouponCodeSuffix14(String date){

        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        //中间6位
        List<Character> chars = date.chars() //变成流操作，然后方便map映射，利用lamada表达式进行每个元素处理
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);//高效的洗牌算法
        String mid6 = chars.stream()
                .map(Object::toString).collect(Collectors.joining());//join 组合的意思，组合成一个字符串。

        //后八位
        String suffix8 = RandomStringUtils.random(1, bases)
                +RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
