package com.wanxiaoyuan.coupon.advice;

import com.wanxiaoyuan.coupon.exception.CouponException;
import com.wanxiaoyuan.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理</h1>
 * Created by WanYue
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * <h2>对 CouponException 进行统一处理</h2>
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(
            HttpServletRequest req, CouponException ex
    ){
       CommonResponse<String> response = new CommonResponse<>(
               -1, "business error"
       );
       response.setData(ex.getMessage());
       return response;
    }
}
