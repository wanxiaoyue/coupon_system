package com.wanxiaoyuan.coupon.dao;

import com.wanxiaoyuan.coupon.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1></h1>
 * Created by WanYue
 */

public interface RolePathMappingRepository
        extends JpaRepository<RolePathMapping, Integer> {

    /**
     * <h2>通过 角色id + 路径id 寻找数据记录</h2>
     */
    RolePathMapping findByRoleIdAndPathId(Integer roleId, Integer pathId);

}
