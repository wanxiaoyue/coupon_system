package com.wanxiaoyuan.coupon;

import com.wanxiaoyuan.coupon.annotation.IgnorePermission;
import com.wanxiaoyuan.coupon.annotation.WanxiaoyuanCouponPermission;
import com.wanxiaoyuan.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <h1>接口权限信息扫描器</h1>
 * Created by WanYue
 */
@Slf4j
public class AnnotationScanner {

     private String pathPrefix;

     private static final String WANXIAOYUAN_COUPON_PKG = "com.wanxiaoyuan.coupon";

     AnnotationScanner(String pathPrefix){
         this.pathPrefix = pathPrefix;

     }

     /**
      * <h2>构造所有 Controller 的权限信息</h2>
      */
     List<PermissionInfo> scanPermission(
             Map<RequestMappingInfo, HandlerMethod> mapping){

         List<PermissionInfo> result = new ArrayList<>();
         mapping.forEach((mapInfo, method) ->
                 result.addAll(buildPermission(mapInfo, method)));

         return result;
     }

     /**
      * <h2>构造 Controller 的权限信息</h2>
      * @param mapInfo {@link RequestMappingInfo} @RequestMapping 对应的信息
      * @param handlerMethod {@link HandlerMethod}  @RequestMapping
      *                                           对应的方法的详情，包括方法、类、参数
      */
     private List<PermissionInfo> buildPermission(
             RequestMappingInfo mapInfo, HandlerMethod handlerMethod
     ){

         Method javaMethod = handlerMethod.getMethod();
         Class baseClass = javaMethod.getDeclaringClass();//找到该方法的类

         // 忽略非 com.wanxiaoyuan.coupon 下的 mapping
         if (!isWanxiaoyuanCouponPackage(baseClass.getName())){
             log.debug("ignore method: {}", javaMethod.getName());
             return Collections.emptyList();
         }

         //判断是否需要忽略此方法
         IgnorePermission ignorePermission = javaMethod.getAnnotation(
                 IgnorePermission.class
         );
         if (null != ignorePermission){
             log.debug("ignore method: {}", javaMethod.getName());
             return Collections.emptyList();
         }

         //取出权限注解
         WanxiaoyuanCouponPermission couponPermission = javaMethod.getAnnotation(
                 WanxiaoyuanCouponPermission.class
         );
         if (null == couponPermission){
             //如果没有标注 WanxiaoyuanCouponPermission 且没有 IgnorePermission, 在日志中记录
              log.error("lack @WanxiaoyuanCouponPermission -> {}#{}",
                      javaMethod.getDeclaringClass().getName(),
                      javaMethod.getName());
              return Collections.emptyList();
         }
         //取出 URL
         Set<String> urlSet = mapInfo.getPatternsCondition().getPatterns();

         //取出 method
         boolean isAllMethods = false;
         Set<RequestMethod> methodSet = mapInfo.getMethodsCondition().getMethods();
         if(CollectionUtils.isEmpty(methodSet)){
             isAllMethods = true;
         }

         List<PermissionInfo> infoList = new ArrayList<>();

         for (String url : urlSet) {

             //支持的 http method 为全量
             if(isAllMethods) {
                 PermissionInfo info = buildPermissionInfo(
                         HttpMethodEnum.ALL.name(),
                         javaMethod.getName(),
                         this.pathPrefix + url,
                         couponPermission.readOnly(),
                         couponPermission.description(),
                         couponPermission.extra()
                 );
                 infoList.add(info);
                 continue;
             }

             //支持部分 http method
             for (RequestMethod method : methodSet) {
                 PermissionInfo info = buildPermissionInfo(
                         method.name(),
                         javaMethod.getName(),
                         this.pathPrefix + url,
                         couponPermission.readOnly(),
                         couponPermission.description(),
                         couponPermission.extra()
                 );
                 infoList.add(info);
                 log.info("permission detected: {}", info);
             }
         }
         return infoList;
     }

     /**
      * <h2>构造单个接口的权限信息</h2>
      */
     private PermissionInfo buildPermissionInfo(
             String reqMethod, String javaMethod, String path,
             boolean readOnly, String desp, String extra
     ){
         PermissionInfo info = new PermissionInfo();
         info.setMethod(reqMethod);
         info.setUrl(path);
         info.setIsRead(readOnly);
         info.setDescription(
                 //如果注解中没有描述，则使用方法名称
                 StringUtils.isEmpty(desp)? javaMethod : desp
         );
         info.setExtra(extra);

         return info;
     }

     /**
      * <h2>判断当前类是否在我们定义的包中， 在pom会引用很多Jar,判断一下是否符合我们的期望代码</h2>
      */
     private boolean isWanxiaoyuanCouponPackage(String className){
         return className.startsWith(WANXIAOYUAN_COUPON_PKG);
     }

     /**
      * <h2>保证 path 以 / 开头，且不以 / 结尾</h2>
      * 如果 user -> /user , /user/ -> /user
      */
     private String trimPath(String path){

         if (StringUtils.isEmpty(path)){
             return "";
         }

         if(!path.startsWith("/")){
             path = "/" + path;
         }

         if(path.endsWith("/")){
             path = path.substring(0, path.length() - 1);
         }
         return path;
     }


}
