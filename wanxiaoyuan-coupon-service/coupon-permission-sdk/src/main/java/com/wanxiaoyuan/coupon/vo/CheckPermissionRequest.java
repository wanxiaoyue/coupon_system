package com.wanxiaoyuan.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>权限校验请求对象定义</h1>
 * Created by WanYue
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckPermissionRequest {

    private Long userId;
    private String uri;
    private String httpMethod;

}
