package com.wanxiaoyuan.coupon.vo;

import lombok.Data;

/**
 * <h1>接口权限信息组装类定义</h1>
 * Created by WanYue
 */
@Data
public class PermissionInfo {

    /** Controller 的URL */
    private String url;

    /** 方法类型 */
    private String method;

    /** 是否只读 */
    private Boolean isRead;

    /** 方法描述信息 */
    private String description;

    /** 扩展属性 */
    private String extra;

    @Override
    public String toString() {
        return "url='" + url +
                ", method='" + method +
                ", isRead=" + isRead +
                ", description='" + description;
    }
}
