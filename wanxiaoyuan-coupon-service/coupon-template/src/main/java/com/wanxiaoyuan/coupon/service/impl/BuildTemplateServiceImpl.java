package com.wanxiaoyuan.coupon.service.impl;

import com.wanxiaoyuan.coupon.dao.CouponTemplateDao;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.service.IAsyncService;
import com.wanxiaoyuan.coupon.service.IBuildTemplateService;
import com.wanxiaoyuan.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <h1>构建优惠券模板接口实现</h1></h1>
 * Created by WanYue
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    /** 异步服务 */
    private final IAsyncService asyncService;
    /** CouponTemplateDao进行增删改查*/
    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService,
                                    CouponTemplateDao couponTemplateDao) {
        this.asyncService = asyncService;
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * <h2>创建优惠券模板</h2>
     * @param request {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠券模板实体
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request)
            throws CouponException {

        //对参数合法性校验
        if(!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }

        //如果已存在模板就不需要弄了，判断同名优惠券是否存在
        if(null != couponTemplateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template");
        }

        //构造CouponTemplate 并保存在数据库中
        CouponTemplate template = requestToTemplate(request);
        template = couponTemplateDao.save(template);

        //根据优惠券模板异步生成优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        //先返回基本信息，所以上一步用的异步创建优惠码，因为它万一要创建几十万个。
        return template;
    }

    /**
     * <h2>将TemplateRequest 转化为 CouponTemplate</h2>
     */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
