package com.wanxiaoyuan.coupon.controller;

import com.wanxiaoyuan.coupon.annotation.IgnoreResponseAdvice;
import com.wanxiaoyuan.coupon.service.IPathService;
import com.wanxiaoyuan.coupon.service.IPermissionService;
import com.wanxiaoyuan.coupon.vo.CheckPermissionRequest;
import com.wanxiaoyuan.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1> 路径创建与权限校验对外服务接口实现 </h1>
 * Created by WanYue
 */

@Slf4j
@RestController
public class PermissionController {


    private final IPathService pathService;
    private final IPermissionService permissionService;

    @Autowired
    public PermissionController(IPathService pathService, IPermissionService permissionService) {
        this.pathService = pathService;
        this.permissionService = permissionService;
    }

    /**
     * <h2>路径创建接口</h2>
     */
    @PostMapping("/create/path")
    public List<Integer> createPath(@RequestBody CreatePathRequest request){

        log.info("createPath: {}", request.getPathInfos().size());
        return pathService.createPath(request);
}

    /**
     * <h2>路径校验接口</h2>
     */
    @IgnoreResponseAdvice
    @PostMapping("/check/permission")
    public Boolean checkPermission(@RequestBody CheckPermissionRequest request){

        log.info("checkPermission for args: {}, {}, {}",
                request.getUserId(), request.getUri(), request.getHttpMethod());
        return permissionService.checkPermission(
                request.getUserId(), request.getUri(), request.getHttpMethod()
        );
    }
}
