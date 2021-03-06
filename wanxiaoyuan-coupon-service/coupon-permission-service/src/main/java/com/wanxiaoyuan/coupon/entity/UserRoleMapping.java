package com.wanxiaoyuan.coupon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * <h1>User 与Role 的映射关系实体类</h1>
 * Created by WanYue
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_user_role_mapping")
public class UserRoleMapping {

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /** User 的主键*/
    @Basic
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Role 的主键*/
    @Basic
    @Column(name = "role_id", nullable = false)
    private Integer roleId;
}
