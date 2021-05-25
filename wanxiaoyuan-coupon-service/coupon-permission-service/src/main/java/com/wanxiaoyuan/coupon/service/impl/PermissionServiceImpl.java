package com.wanxiaoyuan.coupon.service.impl;

import com.wanxiaoyuan.coupon.constant.RoleEnum;
import com.wanxiaoyuan.coupon.dao.PathRepository;
import com.wanxiaoyuan.coupon.dao.RolePathMappingRepository;
import com.wanxiaoyuan.coupon.dao.RoleRepository;
import com.wanxiaoyuan.coupon.dao.UserRoleMappingRepository;
import com.wanxiaoyuan.coupon.entity.Path;
import com.wanxiaoyuan.coupon.entity.Role;
import com.wanxiaoyuan.coupon.entity.RolePathMapping;
import com.wanxiaoyuan.coupon.entity.UserRoleMapping;
import com.wanxiaoyuan.coupon.service.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <h1>权限校验功能服务接口实现</h1>
 * Created by WanYue
 */

@Slf4j
@Service
public class PermissionServiceImpl implements IPermissionService {


    private final PathRepository pathRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final RolePathMappingRepository rolePathMappingRepository;

    @Autowired
    public PermissionServiceImpl(PathRepository pathRepository,
                                 RoleRepository roleRepository,
                                 UserRoleMappingRepository userRoleMappingRepository,
                                 RolePathMappingRepository rolePathMappingRepository) {
        this.pathRepository = pathRepository;
        this.roleRepository = roleRepository;
        this.userRoleMappingRepository = userRoleMappingRepository;
        this.rolePathMappingRepository = rolePathMappingRepository;
    }


    @Override
    public Boolean checkPermission(Long userId, String uri, String httpMethod) {

        UserRoleMapping userRoleMapping = userRoleMappingRepository
                .findByUserId(userId);

        //如果用户角色映射表找不到记录， 直接返回false
        if(null == userRoleMapping) {
            log.error("userId not exist is UserRoleMapping: {}", userId);
            return false;
        }

        // 如果找不到对应的 Role 记录， 直接返回 false, 前面找出了user和role的关系，发现role不属于规定三种也是错误
        Optional<Role> role = roleRepository.findById(userRoleMapping.getRoleId());
        if(!role.isPresent()){
            log.error("roleId not exist in Role: {}",
                    userRoleMapping.getRoleId());
            return false;
        }

        //如果用户觉得是超级管理员，直接返回true
        if(role.get().getRoleTag().equals(RoleEnum.SUPER_ADMIN.name())){
            return true;
        }

        // 如果路径没有注册(忽略了)，直接返回true
        Path path = pathRepository.findByPathPatternAndHttpMethod(
                uri, httpMethod
        );//唯一确定路径。  不一定能在Path中找到，比如健康功能就不会在path表
        if(null == path){
            return true;
        }

        RolePathMapping rolePathMapping = rolePathMappingRepository
                .findByRoleIdAndPathId(
                        role.get().getId(), path.getId()
                );
        return rolePathMapping != null;

    }
}
