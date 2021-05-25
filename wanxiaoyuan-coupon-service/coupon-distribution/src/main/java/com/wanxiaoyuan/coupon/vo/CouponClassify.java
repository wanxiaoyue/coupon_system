package com.wanxiaoyuan.coupon.vo;

import com.wanxiaoyuan.coupon.constant.CouponStatus;
import com.wanxiaoyuan.coupon.constant.PeriodType;
import com.wanxiaoyuan.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>用户优惠券的分类，根据优惠券的状态</h1>
 * Created by WanYue
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    /** 可以使用的*/
    private List<Coupon> usable;

    /** 已使用的*/
    private List<Coupon> used;

    /** 已过期的*/
    private List<Coupon> expired;

    /**
     * <h2>对当前的优惠券进行分类</h2>
     */
    public static CouponClassify classify(List<Coupon> coupons){

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            boolean isTimeExpired;
            long curTime = new Date().getTime();

            //看是否为固定过期时间 还是 相对过期时间
            if(c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )){
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadline()<=curTime;
            }else {
                isTimeExpired = DateUtils.addDays(
                        c.getAssignTime(),
                        c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime() <= curTime;
            }

            if(c.getStatus() == CouponStatus.USED){
                used.add(c);
            }else if(c.getStatus() == CouponStatus.EXPIRED || isTimeExpired){
                expired.add(c);
            }else{
                usable.add(c);
            }
        });
        return new CouponClassify(usable, used, expired);
    }
}
