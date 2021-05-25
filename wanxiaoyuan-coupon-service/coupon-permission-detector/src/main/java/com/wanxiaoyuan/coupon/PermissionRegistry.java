package com.wanxiaoyuan.coupon;

import com.wanxiaoyuan.coupon.permission.PermissionClient;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import com.wanxiaoyuan.coupon.vo.CreatePathRequest;
import com.wanxiaoyuan.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>权限注册组件</h1>
 * Created by WanYue
 */
@Slf4j
public class PermissionRegistry {

    /** 权限服务 SDK 客服端 */
    private PermissionClient permissionClient;

    /** 服务名称 */
    private String serviceName;

    /**
     * <h2>构造方法</h2>
     */
    public PermissionRegistry(PermissionClient permissionClient, String serviceName) {
        this.permissionClient = permissionClient;
        this.serviceName = serviceName;
    }

    /**
     * <h2>权限注册</h2>
     */
    boolean register(List<PermissionInfo> infoList)
    {
        if (CollectionUtils.isEmpty(infoList)){
            return false;
        }

        List<CreatePathRequest.PathInfo> pathInfos = infoList.stream()
                .map(info -> CreatePathRequest.PathInfo.builder()//builder的注释作用是不停的去设置该类的属性
                .pathPattern(info.getUrl())
                        .httpMethod(info.getMethod())
                        .pathName(info.getDescription())
                        .serviceName(serviceName)
                        .opMode(info.getIsRead() ? OpModeEnum.READ.name() :
                                OpModeEnum.WRITE.name())
                        .build()
                ).collect(Collectors.toList());

        CommonResponse<List<Integer>> response = permissionClient.createPath(
                new CreatePathRequest(pathInfos)
                );

        if (!CollectionUtils.isEmpty(response.getData())){
            log.info("register path info: {}", response.getData());
            return true;
        }
        return  false;
    }
}
