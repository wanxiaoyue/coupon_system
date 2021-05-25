package com.wanxiaoyuan.coupon.service;

import com.wanxiaoyuan.coupon.vo.CreatePathRequest;

import java.util.List;

/**
 * <1> 路径相关的服务功能接口定义 </1>
 * Created by WanYue
 */

public interface IPathService {

    /**
     * <h2>添加新的 path 到数据表中</h2>
     */
    List<Integer> createPath(CreatePathRequest request);

}
