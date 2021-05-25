package com.wanxiaoyuan.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>路径创建请求对象定义</h1>
 * Created by WanYue
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePathRequest {

    private List<PathInfo> pathInfos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathInfo{

        /** 路径模式*/
        private String pathPattern;

        /** HTTP 方式类型 */
        private String httpMethod;

        /** 路径名称 */
        private String pathName;

        /** 服务名称 */
        private String serviceName;

        /** 操作模式：READ, WRITE */
        private String opMode;

    }
}
