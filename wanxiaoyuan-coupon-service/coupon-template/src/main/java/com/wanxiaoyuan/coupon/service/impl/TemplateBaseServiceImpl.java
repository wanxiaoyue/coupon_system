package com.wanxiaoyuan.coupon.service.impl;

import com.wanxiaoyuan.coupon.dao.CouponTemplateDao;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.service.ITemplateBaseService;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>根据</h1>
 * Created by WanYue
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    /** TemplateDao接口方便增删改查 */
    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * <h2>根据优惠券模板 id 获取优惠券模板信息</h2>
     * @param id 模板 id
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {

        Optional<CouponTemplate> template = couponTemplateDao.findById(id);

        if(!template.isPresent()){
            throw new CouponException("Template Is Not Exist: " + id);
        }

        return template.get();
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     * @return {@link CouponTemplateSDK} s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> templates =
                couponTemplateDao.findAllByAvailableAndExpired(
                        true, false);//可用并没过期，可能返回已加过期但是定时器还没有处理，所以客户端还要处理一次

        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * <h2>获取模板 Ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids 模板 ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {

        //如果传入不存在的Id,List里面只是一个长度为0的数组，不是为Null
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);

        return templates.stream().map(this::template2TemplateSDK)
                .collect(Collectors.toMap(
                        CouponTemplateSDK::getId, Function.identity()
                ));
    }

    /**
     * <h2>将 CouponTemplate 转换为 CouponTemplateSDK</h2>
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template){

        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(), //并不是拼装好的 Template Key
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
