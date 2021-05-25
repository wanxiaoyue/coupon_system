package com.wanxiaoyuan.coupon.filter;

import com.alibaba.fastjson.JSON;
import com.wanxiaoyuan.coupon.permission.PermissionClient;
import com.wanxiaoyuan.coupon.vo.CheckPermissionRequest;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h1>权限过滤器的实现</h1>
 * Created by WanYue
 */
@Slf4j
//@Component
public class PermissionFilter extends AbsSecurityFilter {

    private final PermissionClient permissionClient;

    @Autowired
    public PermissionFilter(PermissionClient permissionClient) {
        this.permissionClient = permissionClient;
    }

    @Override
    protected Boolean interceptCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 执行权限校验的逻辑
        //能够获取这三个信息，我们就去构造校验请求，然后去进行校验
        // 从 Header 中获取到 userId, 也可用从cookie与session获取
        Long userId = Long.valueOf(request.getHeader("userId"));
        String uri = request.getRequestURI().substring("/wanxiaoyuan".length());
        String httpMethod = request.getMethod();

        return permissionClient.checkPermission(
                new CheckPermissionRequest(userId, uri, httpMethod)
        );
    }

    @Override
    protected int getHttpStatus() {
        //返回404，不友好， 返回正确反应码，如果有错误，在错误信息里体现
        return HttpStatus.SC_OK;
    }

    @Override
    protected String getErrorMsg() {

        CommonResponse<Object> response = new CommonResponse<>();
        response.setCode(-2);
        response.setMessage("您没有操作权限");

        return JSON.toJSONString(response);
    }
}
