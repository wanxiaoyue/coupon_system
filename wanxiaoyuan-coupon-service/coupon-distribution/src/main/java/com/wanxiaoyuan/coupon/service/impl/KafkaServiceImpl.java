package com.wanxiaoyuan.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.Constant;
import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.dao.CouponDao;
import com.wanxiaoyuan.coupon.entity.Coupon;
import com.wanxiaoyuan.coupon.service.IKafkaService;
import com.wanxiaoyuan.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <h1>Kafka 相关的服务几口实现</h1>
 * 核心思想： 试讲 Cache 中的 coupon 的状态变化同步到 DB 中，  我们操作用户数据都是用Redis,所以要把Redis的结果弄到数据库中
 * Created by WanYue
 */
@Slf4j
@Service //在spring中，spring接收到消息，通知Kafka
public class KafkaServiceImpl implements IKafkaService {

    //要进行数据库，处理注入Dao
    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    /**
     * <h2>消费优惠券 Kafka 消息</h2>
     * @param record {@link ConsumerRecord}
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "wanxiaoyuan-coupon-1") //当kafka检测到Topic有消息来得时候，他会把消息反序列化为record，然后再进行我们自己的逻辑消费
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        //获得kafka消息
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafkaMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status){
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }
    }

    /**
     * <h2>处理已使用的用户优惠券</h2>
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status){
        //TODO 给用户发送短信，表明消费成功
        processCouponByStatus(kafkaMessage, status);
    }

    /**
     * <h2>处理已过期的用户优惠券信息</h2>
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage,
                                    CouponStatus status){
        //TODO 给用户发送推送，说这里还有更多的消费券，来领哦
        processCouponByStatus(kafkaMessage, status);
    }
    /**
     * <h2>根据状态处理优惠券信息</h2>
     */
    private void processCouponByStatus(CouponKafkaMessage kafkaMessage,
                                       CouponStatus status){
        List<Coupon> coupons = couponDao.findAllById(
                kafkaMessage.getIds()
        );
        if(CollectionUtils.isEmpty(coupons)
                || coupons.size() != kafkaMessage.getIds().size()){
            log.error("Can Not Find Right Coupon Info: {}",
                    JSON.toJSONString(kafkaMessage));
            //TODO 发送邮件
            return;
        }

        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafkaMessage Op Coupon Count: {]",
                couponDao.saveAll(coupons).size());
    }
}
