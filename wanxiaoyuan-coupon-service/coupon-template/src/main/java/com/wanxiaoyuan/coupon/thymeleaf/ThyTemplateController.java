package com.wanxiaoyuan.coupon.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.constant.*;
import com.wanxiaoyuan.coupon.dao.CouponTemplateDao;
import com.wanxiaoyuan.coupon.entity.CouponTemplate;
import com.wanxiaoyuan.coupon.service.IBuildTemplateService;
import com.wanxiaoyuan.coupon.vo.TemplateRequest;
import com.wanxiaoyuan.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>优惠券模板 Controller</h1>
 * Created by Qinyi.
 */
@Slf4j
@Controller
@RequestMapping("/template/thy")
public class ThyTemplateController {

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    /** 构造优惠券模板服务 */
    private final IBuildTemplateService templateService;

    @Autowired
    public ThyTemplateController(CouponTemplateDao templateDao, IBuildTemplateService templateService) {
        this.templateDao = templateDao;
        this.templateService = templateService;
    }

    /**
     * <h2>优惠券系统入口</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/home
     * */
    @GetMapping("/home")
    public String home() {

        log.info("view home.");
        return "home";
    }

    /**
     * <h2>查看优惠券模板详情</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/info/{id}
     * */
    @GetMapping("/info/{id}")
    public String info(@PathVariable Integer id, ModelMap map) {

        log.info("view template info.");
        Optional<CouponTemplate> templateO = templateDao.findById(id);
        if (templateO.isPresent()) {
            CouponTemplate template = templateO.get();
            map.addAttribute("template", ThyTemplateInfo.to(template));
        }

        return "template_detail";
    }

    /**
     * <h2>查看优惠券模板列表</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/list
     * */
    @GetMapping("/list")
    public String list(ModelMap map) {

        log.info("view template list.");
        List<CouponTemplate> couponTemplates = templateDao.findAll();
        List<ThyTemplateInfo> templates =
                couponTemplates.stream().map(ThyTemplateInfo::to).collect(Collectors.toList());

        map.addAttribute("templates", templates);
        return "template_list";
    }

    /**
     * <h2>创建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/create
     * */
    @GetMapping("/create")
    public String create(ModelMap map, HttpSession session) {

        log.info("view create form.");

        session.setAttribute("category", CouponCategory.values());
        session.setAttribute("productLine", ProductLine.values());
        session.setAttribute("target", DistributeTarget.values());
        session.setAttribute("period", PeriodType.values());
        session.setAttribute("goodsType", GoodsType.values());

        map.addAttribute("template", new ThyCreateTemplate());
        map.addAttribute("action", "create");
        return "template_form";
    }

    /**
     * <h2>创建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/create
     * */
    @PostMapping("/create")
    public String create(@ModelAttribute ThyCreateTemplate template) throws Exception {

        log.info("create form.");
        log.info("{}", JSON.toJSONString(template));

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                template.getPeriod(), template.getGap(),
                new SimpleDateFormat("yyyy-MM-dd").parse(template.getDeadline()).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(template.getQuota(), template.getBase()));
        rule.setLimitation(template.getLimitation());
        rule.setUsage(new TemplateRule.Usage(template.getProvince(), template.getCity(),
                JSON.toJSONString(template.getGoodsType())));
        rule.setWeight(
                JSON.toJSONString(Stream.of(template.getWeight().split(",")).collect(Collectors.toList()))
        );

        TemplateRequest request = new TemplateRequest(
                template.getName(), template.getLogo(), template.getDesc(),
                template.getCategory(), template.getProductLine(), template.getCount(),
                template.getUserId(), template.getTarget(), rule
        );

        log.info("create coupon template: {}", JSON.toJSONString(templateService.buildTemplate(request)));

        return "redirect:/template/thy/list";
    }
}
