package com.wanxiaoyuan.coupon;

import com.wanxiaoyuan.coupon.permission.PermissionClient;
import com.wanxiaoyuan.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * <h1>权限探测监听器, Spring 容器启动之后自动启动 </h1>
 * Created by WanYue
 */
@Slf4j
@Component
public class PermissionDetectListener implements  ApplicationListener<ApplicationReadyEvent>{

    //找到微服务路径和名称
    private static final String KEY_SERVER_CTX = "server.servlet.context-path";
    private static final String KEY_SERVICE_NAME = "spring.application.name";


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //拿到spring
        ApplicationContext ctx = event.getApplicationContext();

        //为了不影响主线程， 这边另外一个线程扫描权限
        new Thread(() ->{

            //扫描权限（注解)
            List<PermissionInfo> infoList = scanPermission(ctx);

            //注册权限
            registerPermission(infoList, ctx);

        }).start();

    }

    /**
     * <h2>注册接口权限</h2>
     */
    private void registerPermission(List<PermissionInfo> infoList,
                                    ApplicationContext ctx){
        log.info("************** register permission ****************");

        PermissionClient permissionClient = ctx.getBean(PermissionClient.class);
        if (null == permissionClient) {
            log.error("no permissionClient bean found");
            return;
        }

        // 取出 service name
        String serviceName = ctx.getEnvironment().getProperty(KEY_SERVICE_NAME);

        log.info("serviceName: {}", serviceName);

        boolean result = new PermissionRegistry(
                permissionClient, serviceName
        ).register(infoList);

        if(result){
            log.info("************** done register ****************");
        }

    }

    /**
     * <h2>扫描微服务中的 Controller 接口权限信息</h2>
     */
    private List<PermissionInfo> scanPermission(ApplicationContext ctx){ //ApplicationContext 就是Spring容器

        //取出 context 前缀
        String pathPrefix = ctx.getEnvironment().getProperty(KEY_SERVER_CTX);

        //取出 Spring 处理器的映射 bean  MVC那一套流程
        RequestMappingHandlerMapping mappingBean =
                (RequestMappingHandlerMapping) ctx.getBean("requestMappingHandlerMapping");

        //扫描权限
        List<PermissionInfo> permissionInfoList =
                new AnnotationScanner(pathPrefix).scanPermission(
                        mappingBean.getHandlerMethods()
                );
        permissionInfoList.forEach(
                p -> log.info("{}", p));
        log.info("{} permission found", permissionInfoList.size());
        log.info("************** done scanning ****************");

        return permissionInfoList;
    }
}
