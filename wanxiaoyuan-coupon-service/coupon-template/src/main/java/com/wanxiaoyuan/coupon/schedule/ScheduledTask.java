package com.wanxiaoyuan.coupon.schedule;

import com.wanxiaoyuan.coupon.dao.CouponTemplateDao;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>定时清理已过期的优惠券模板</h1>
 * Created by WanYue
 */
@Slf4j
@Component
public class ScheduledTask {

    /** CouponTemplateDao*/
    private final CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * <h2>下线已经过期的优惠券模板</h2>  1000是Ms
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate(){

        log.info("Start To Expire CouponTemplate");

        List<CouponTemplate> templates =
                templateDao.findAllByExpired(false);

        if(CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate.");
            return;
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates =
                new ArrayList<>(templates.size());

        templates.forEach(t ->{
            //根据优惠券模板规则中的“过期规则”校验模板
            TemplateRule rule = t.getRule();
            if(rule.getExpiration().getDeadline() < cur.getTime()){
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });

        if(!CollectionUtils.isEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num: {}",
                    templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate.");
    }
}
