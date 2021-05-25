package com.wanxiaoyuan.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.Constant;
import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.dao.CouponDao;
import com.wanxiaoyuan.coupon.entity.Coupon;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.feign.SettlementClient;
import com.wanxiaoyuan.coupon.feign.TemplateClient;
import com.wanxiaoyuan.coupon.service.IRedisService;
import com.wanxiaoyuan.coupon.service.IUserService;
import com.wanxiaoyuan.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>用户服务相关的接口实现</h1>
 * 所有的操作过程和状态都保存在redis中，并通过 Kafka 把消息传递到Mysql 中做异步处理
 * 为什么使用 Kafka， 而不是直接使用 SpringBoot 中的异步处理？  主要是安全性， 异步任务是可能会失败的，传递到kafka消息失败了，也能重新
 * 从kafka 获取消息，回退记录，保证redis和mysql的一致性
 * Created by WanYue
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    /** Coupon Dao*/
    private final CouponDao couponDao;

    /** Redis 服务*/
    private final IRedisService redisService;

    /** 模板微服务客户端*/
    private final TemplateClient templateClient;

    /** 结算微服务客户端*/
    private final SettlementClient settlementClient;

    /** Kafka客户端 */
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao,
                           IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate
                         ) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     * <h2>根据用户 id 和状态查询优惠券记录</h2>
     *
     * @param userId 用户 id
     * @param status 优惠券状态
     * @return {@link Coupon}s
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {

        //如果是Null,就会返回一个空数据到缓存
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        if(CollectionUtils.isNotEmpty(curCached)){
            log.debug("coupon cache is not empty = {}, {}", userId, status);
            preTarget = curCached;
        }else {
            log.debug("coupon cache is empty, get coupon from db: {}, {}",
                    userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(
                    userId, CouponStatus.of(status)
            );
            // 如果数据库中没有记录， 直接返回就可以， Cache 总已经加入了一张无效的优惠券
            if(CollectionUtils.isEmpty(dbCoupons)){
                log.debug("current user do not have coupon: {}, {}", userId, status);
                return dbCoupons;
            }
            //填充 dbCoupons的 templateSKD 字段, 这个templateSDK的属性怎么来， 模板微服务来的
            Map<Integer, CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(
                            dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
                    ).getData();
            dbCoupons.forEach(
                    dc -> dc.setTemplateSDK(
                            id2TemplateSDK.get(dc.getTemplateId())
                    )
            );
            //数据库中存在记录
            preTarget = dbCoupons;
            //将数据库的内容记录写入 Cache
            redisService.addCouponToCache(
                    userId, preTarget, status
            );
        }

        //将无效优惠券剔除
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != -1)
                .collect(Collectors.toList());

        //如果当前获的是可用优惠券，还需要做对已过期优惠券的延迟处理，  判断是否当前优惠券是否过期，有延时
        if(CouponStatus.of(status) == CouponStatus.USABLE){
            CouponClassify classify = CouponClassify.classify(preTarget);
            //如果已过期状态不为空，需要做延迟处理
            if(CollectionUtils.isNotEmpty(classify.getExpired())){
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus:" +
                        "{} {}", userId, status);
                redisService.addCouponToCache(
                        userId, classify.getExpired(),
                        CouponStatus.EXPIRED.getCode()
                );
                //发送到Kafka中做异步处理，因为可用的优惠券由于延时成了过期优惠券
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired()
                                        .stream()
                                .map(Coupon::getId)
                                        .collect(Collectors.toList())
                        ))
                );
            }
            return classify.getUsable();
        }

        return preTarget;
    }

    /**
     * <h2>根据用户 id 查找当前可以领取的优惠券模板</h2>
     *
     * @param userId 用户id
     * @return {@link Coupon}s
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId)
            throws CouponException {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS =
                templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template(From TemplateClient) Count = {}",
                templateSDKS.size());

        //过滤过期的优惠券模板
        templateSDKS = templateSDKS.stream().filter(
                t-> t.getRule().getExpiration().getDeadline() > curTime
        ).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        //key 是 TemplateId
        //value 中的 left 是 Template limitation, right 是优惠券模板
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template =
                new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t -> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                )
        );

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());

        List<Coupon> userUsableCoupons = findCouponsByStatus(
                userId, CouponStatus.USABLE.getCode()
        );

        log.debug("Current User Has Usable Coupons: {},{}", userId,
                userUsableCoupons.size());

        //key 是 TemplateID ,value用户已经领取的模板id
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        //根据 Template 的 Rule判断是否可以领取优惠券模板
        limit2Template.forEach((k, v) ->{

            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();

            //如果该用户领取的优惠券模板的优惠券数大于规定的数量就直接返回，不能领取
            if(templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation){
                return;
            }

            //没有达到就可以继续领取
            result.add(templateSDK);

        });

        return result;
    }

    /**
     * 从前端传来的参数不要就觉得很有效，一定要多判断
     * <h2>用户领取优惠券</h2>
     *1. 从 TemplateClient 拿到对应的优惠券，并检查是否过期
     *2. 根据limitation 判断用户
     *3. save to db
     * 4. 填充 CouponTemplateSDK
     * 5. save to cache
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {

        Map<Integer, CouponTemplateSDK> id2Template =
                templateClient.findIds2TemplateSDK(
                        Collections.singletonList(
                                request.getTemplateSDK().getId()
                        )
                ).getData();

        //优惠券模板是需要存在的
        if(id2Template.size() <= 0){
            log.error("Can Not Acquire Template From TemplateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }

       //用户是否可以领取这张优惠券
        //找出用户用可用的模板id
        List<Coupon> userUsableCoupons = findCouponsByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode()
        );

        //将templateId一样的放在一起，一个k，对应一个value，这个value里面是一个List
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        if(templateId2Coupons.containsKey(request.getTemplateSDK().getId())
        && templateId2Coupons.get(request.getTemplateSDK().getId()).size() >=
                request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed Template Assign Limitation: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation: {}");
        }

        //尝试去获取优惠券码
        //秒杀的时候非常常用，有些是不能拿到的
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(
                request.getTemplateSDK().getId()
        );
        if(StringUtils.isEmpty(couponCode)){
            log.error("Can Not Acquire Coupon Code: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon newCoupon = new Coupon(
                request.getTemplateSDK().getId(), request.getUserId(),
                couponCode, CouponStatus.USABLE
        );
        newCoupon = couponDao.save(newCoupon);

        //填充 Coupon 对象的 CouponTemplateSDK, 一定要在放入缓存之前去填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        //放入缓存
        redisService.addCouponToCache(
                request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode()
        );
        return null;
    }

    /**
     * <h2>结算(核销)优惠券</h2>
     * 这里需要注意，规则相关处理需要由 Settlement 系统去做，当前系统仅仅做业务处理过程(校验过程)
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {

        //当没有传递优惠券时， 直接返回商品总价
       List<SettlementInfo.CouponAndTemplateInfo>  ctInfos =
               info.getCouponAndTemplateInfos();
       if(CollectionUtils.isEmpty(ctInfos)){

           log.info("Empty Coupons For Settle.");
           double goodsSum = 0.0;

           //个数*总价
           for(GoodsInfo gi : info.getGoodsInfos()){
               goodsSum += gi.getPrice() * gi.getCount();
           }

           //没有优惠券也就不存在优惠券的核销，SettlementInfo 其他的字段不需要修改
           info.setCost(retain2Decimals(goodsSum));
       }

       //检验传递的优惠券是否是用户自己的, 不轻易信任
        List<Coupon> coupons = findCouponsByStatus(
                info.getUserId(), CouponStatus.USABLE.getCode()
        );
       Map<Integer, Coupon> id2Coupon = coupons.stream()
               .collect(Collectors.toMap(
                       Coupon::getId,      //是优惠券id,直接用coupons.getId()还不行
                       Function.identity()//value 是它本身
               ));

       //如果优惠券是空 或者 结算用户的优惠券id是否是为数据库中的该用户拥有的优惠券id
       if (MapUtils.isEmpty(id2Coupon) || !CollectionUtils.isSubCollection(
               ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
               .collect(Collectors.toList()), id2Coupon.keySet()
       )){
           log.info("{}", id2Coupon.keySet());
           log.info("{}", ctInfos.stream()
                          .map(SettlementInfo.CouponAndTemplateInfo::getId)
                          .collect(Collectors.toList()));
           log.error("User Coupon Has Some Problem, It Is Not SubCollection"+ "of Coupons!");

           throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection"+ "of Coupons!");
       }

       log.debug("Current Settlement Coupons Is User's: {]", ctInfos.size());

       List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
       ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci.getId())));

       //通过结算服务获取结算信息
        SettlementInfo processedInfo =
                settlementClient.couponRule(info).getData();

        if(processedInfo.getEmploy() && CollectionUtils.isNotEmpty(
                processedInfo.getCouponAndTemplateInfos()
        )){
            log.info("Settle User Coupon: {}， {}", info.getUserId(),
                    JSON.toJSONString(settleCoupons));
            //更新缓存
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            //更新 db
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId)
                            .collect(Collectors.toList())
                    ))
            );
        }
        return processedInfo;
    }

    /**
     * <h2>保留两位小数</h2>
     */
    private double retain2Decimals(double value){

        //BigDecimal.ROUND_HALF_UP
        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_UP)
                .doubleValue();
    }
}
