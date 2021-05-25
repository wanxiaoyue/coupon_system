package com.wanxiaoyuan.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.Constant;
import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.entity.Coupon;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * <h1>Redis 相关的操作服务接口实现</h1>
 * Created by WanYue
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    /** Redis 客户端*/
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据 userId 和状态找到缓存的优惠券列表数据</h2>
     * 目的避免Redis的缓存穿透，多一次了Mysql查询。第一次查询塞一个空对象，下次就直接查询出来
     * @param userId 用户 id
     * @param status 优惠券状态 {@link CouponStatus}
     * @return {@link Coupon} 注意，可能会返回 Null, 代表从来没有记录过
     */
    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {

        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = status2RedisKey(status, userId);

        //因为value是Obejct 转为 String
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());

        //如果为空，设置一个空对象
        if(CollectionUtils.isEmpty(couponStrs)){
            saveEmptyCouponListToCache(userId,
                    Collections.singletonList(status));
            return Collections.emptyList();
        }
        //不为空就返回对象
        return couponStrs.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * <h2>保存空的优惠券列表到缓存中</h2> 避免缓存穿透，防止去数据库查找
     *
     * @param userId 用户 id
     * @param status 优惠券状态列表
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List To Cache For User: {} , Status: {}",
                userId, JSON.toJSONString(status));

        //key是 coupon_id, value 是序列化的 Coupon
        Map<String, String> invalidCouponMap = new HashMap<>();
        //塞入一条无效信息,避免缓存穿透
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠券缓存信息
        //KV
        //K: status -> redisKey
        //V: {coupon_id, 序列化的 Coupon}
        //字典forHash
        //使用 SessionCallback 把数据命令放到Redis 的pipeline
        //Redis，单线程 一条一条命令执行， 把所有指令发送到pipeline就可以一起执行，不用等一条一条执行的返回时间
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                        }
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * <h2>尝试从 Cache 中获取一个优惠券码</h2>
     * @param templateId 优惠券模板主键
     * @return 优惠券码， 也可能为Null
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {

        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,templateId.toString());
        //因为优惠券码不存在顺序关系，左边pop或右边pop，没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        //这里可能会Null,这里判断空是在user实现类实现。
        log.info("Acquire Coupon Code: {}, {}, {}",
                templateId, redisKey, couponCode);
        return couponCode;
    }

    /**
     * <h2>将优惠券保存到 Cache 中</h2>
     *
     * @param userId  用户 Id
     * @param coupons {@link Coupon}s
     * @param status  优惠券状态，只有没使用的才行。
     * @return 保存成功的个数
     * @throws CouponException
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {

        log.info("Add Coupon To Cache: {}, {}, {]",
                userId, JSON.toJSONString(coupons), status);

        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus){
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }

        return result;
    }

    /**
     * <h2>新增加优惠券到 Cache 中</h2>
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons){

        // 如果status 是 USABLE， 代表是新增的优惠券
        //只会影响一个 Cache: USER_COUPON_USABLE
        log.debug("Add Coupon To Cache For Usable.");

        Map<String, String> needCacheObject = new HashMap<>();
        coupons.forEach(c ->
                needCacheObject.put(
                        c.getId().toString(),
                        JSON.toJSONString(c)
                ));

        String redisKey = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCacheObject);
        log.info("Add {} Coupons To Cache: {}, {}",
                needCacheObject.size(), userId, redisKey);
        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1, 2), //在这个里面已经*60*60  一个小时到两个小时过期，防止统一过期都去查询数据库
                TimeUnit.SECONDS
        );

        //返回存储个数
        return needCacheObject.size();

    }

    /**
     * <h2>将已使用的优惠券加入到 Cache 中</h2>
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons)
        throws CouponException{

        //如果 status 是USED， 代表用户是使用当前的优惠券， 影响到两个 Cache
        //USABLE, USED  只有可用才能被使用

        log.info("Add Coupon To Cache For Used.");

        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForUsed = status2RedisKey(
                CouponStatus.USED.getCode(), userId
        );
        //获取当前用户可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId, CouponStatus.USABLE.getCode()
        );
        //当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size(); //里面始终多一个无效的优惠券 满足这个情况才会往下走

        coupons.forEach(c -> needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));

        //校验当前的优惠券参数是否参与 Cached 中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        //防止过期的优惠券不存在总的优惠券集合，避免Bug  B是否是A的子集
        if(!CollectionUtils.isSubCollection(paramIds, curUsableIds)){
            log.error("CurCoupons Is Not Equal ToCache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache!");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public  Objects execute(RedisOperations redisOperations) throws DataAccessException {

                //1. 已使用的优惠券 Cache 缓存
                redisOperations.opsForHash().putAll(
                        redisKeyForUsed, needCachedForUsed
                );
                //2. 可用的优惠券 Cache 需要清理
                redisOperations.opsForHash().delete(
                        redisKeyForUsable, needCleanKey.toArray());
                //3. 重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS);
                redisOperations.expire(
                        redisKeyForUsed,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS);

                return null;
            }
        };
     log.info("PipeLine Exe Result: {}",
             JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
     return coupons.size();
    }

    /**
     * <h2>将过期优惠券加入到 Cache 中</h2>
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons)
            throws CouponException{

        // status 是 EXPIRED, 代表是已有的优惠券过期了， 影响到两个 Cache
        // USABLE, EXPIRED

        log.info("Add Coupon To Cache For Expired.");

        // 最终需要保存的 Cache
        Map<String, String> needCachedForExpired = new HashMap<>(coupons.size());

        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId, CouponStatus.USABLE.getCode()
        );


        //当前可用的优惠券个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(
                c -> needCachedForExpired.put(
                        c.getId().toString(), JSON.toJSONString(c)
                ));

        // 校验当前的优惠券参数是否与 Cached 中的匹配, 不能相信传递的参数一定有效的
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if(!CollectionUtils.isSubCollection(paramIds, coupons)){
            log.error("CurCoupons Is Not Equal To Cache: {}, {}, {}",
                    userId, JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupon Is Not Equal To Cache!");
        }

        //变成String 方便 Redis操作
        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());

        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1. 已过期的优惠券 Cache 缓存
                redisOperations.opsForHash().putAll(
                        redisKeyForExpired, needCachedForExpired
                );
                //2. 可用优惠券 Cache 需要清理
                redisOperations.opsForHash().delete(
                        redisKeyForUsable, needCleanKey.toArray()
                );
                //3. 重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1, 2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",
                JSON.toJSONString(
                        redisTemplate.executePipelined(sessionCallback)
                ));
        return coupons.size();
    }

    /**
     * <h2>根据 status 获取到对应的 Redis Key</h2>
     */
    private String status2RedisKey(Integer status, Long userId){

        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);

        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.UER_COUPON_USABLE,userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED,userId);
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED,userId);
                break;
        }
        return redisKey;
    }

    /**
     * <h2>获取一个随机的过期时间</h2> 防止Key具有相同的过期时间，免得查不到，全去查询Mysql
     * 缓存雪崩： key 在同一时间失效
     * @param min 最小的小时数
     * @param max 最大的小时数
     * @return 返回 [min, max] 之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max){

        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60
        );
    }
}
