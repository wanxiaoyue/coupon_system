package com.wanxiaoyuan.coupon.permission;

import com.wanxiaoyuan.coupon.vo.CheckPermissionRequest;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.CreatePathRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * <h1>路径创建与权限校验功能 Feign 接口实现</h1>
 * Created by WanYue
 */
@FeignClient(value = "eureka-client-coupon-permission")
public interface PermissionClient  {

    @RequestMapping(value = "/coupon-permission/create/path",
    method = RequestMethod.POST)
    CommonResponse<List<Integer>> createPath(
            @RequestBody CreatePathRequest request
            );

    @RequestMapping(value = "/coupon-permission/check/permission", method = RequestMethod.POST)
    Boolean checkPermission (@RequestBody CheckPermissionRequest request);
}
