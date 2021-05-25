package com.wanxiaoyuan.coupon.service;

/**
 * <h1>权限校验功能服务接口定义</h1>
 * Created by WanYue
 */

public interface IPermissionService {

Boolean checkPermission(Long userId, String uri, String httpMethod);
}
