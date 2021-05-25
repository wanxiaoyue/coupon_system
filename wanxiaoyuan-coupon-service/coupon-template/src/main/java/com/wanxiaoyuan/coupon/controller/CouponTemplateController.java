package com.wanxiaoyuan.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.annotation.IgnorePermission;
import com.wanxiaoyuan.coupon.annotation.WanxiaoyuanCouponPermission;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.service.IBuildTemplateService;
import com.wanxiaoyuan.coupon.service.ITemplateBaseService;
import com.wanxiaoyuan.coupon.vo.CouponTemplateSDK;
import com.wanxiaoyuan.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠券模板相关的功能控制器</h1>
 * Created by WanYue
 */

@Slf4j
@RestController
public class CouponTemplateController {

    /** 构建优惠券模板服务*/
    private final IBuildTemplateService buildTemplateService;

    /** 优惠券模板基础服务*/
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * <h2>构建优惠券模板</h2>
     * 127.0.0.1：7001/coupon-template/template/build
     * 127.0.0.1：9000/wanxiaoyuan/coupon-template/template/build
     * 启动微服务，网关，启动服务
     */
    @PostMapping("/template/build") //表单提交需要Post
    @WanxiaoyuanCouponPermission(description = "buildTemplate", readOnly = false)
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException{
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * <h2>查看构造优惠券模板详情</h2>
     * 127.0.0.1：7001/coupon-template/template/info
     * 127.0.0.1:9000/wanxiaoyuan/coupon-template/template/info?id=1
     */
    @GetMapping("/template/info")
    @WanxiaoyuanCouponPermission(description = "buildTemplateInfo")
    public CouponTemplate buildTemplateInfo(@RequestParam Integer id) throws CouponException{
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * <h2>查找所有可用的优惠券模板</h2>
     *  127.0.0.1：7001/coupon-template/template/sdk/all
     *  127.0.0.1:9000/wanxiaoyuan/coupon-template/template/sdk/all
     */
    @GetMapping("/template/sdk/all")
    @IgnorePermission  //任何人都可用去访问这个查询，不会在数据库中生成权限路径
    public List<CouponTemplateSDK> findAllUsableTemplate(){

        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * <h2>获取模板 ids CouponTemplateSDK 的映射</h2>
     * 127.0.0.1：7001/coupon-template/template/sdk/infos
     * 127.0.0.1:9000/wanxiaoyuan/coupon-template/template/sdk/infos?ids=1,2
     */
    @GetMapping("/template/sdk/infos")
    @IgnorePermission
    //如果不添加权限注解，会报error
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids){
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
