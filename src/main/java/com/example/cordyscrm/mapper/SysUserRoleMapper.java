package com.example.cordyscrm.mapper;

import com.example.cordyscrm.entity.SysUserRole;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    @Select("SELECT user_id AS userId, role_id AS roleId, created_at AS createdAt FROM sys_user_role ORDER BY created_at DESC")
    List<SysUserRole> findAll();

    @Select("SELECT user_id AS userId, role_id AS roleId, created_at AS createdAt FROM sys_user_role WHERE user_id=#{userId} AND role_id=#{roleId}")
    SysUserRole find(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Insert("INSERT INTO sys_user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_user_role WHERE user_id=#{userId} AND role_id=#{roleId}")
    int delete(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT COUNT(*) FROM sys_user_role ur " +
            "JOIN sys_role r ON r.id=ur.role_id " +
            "JOIN sys_user u ON u.id=ur.user_id " +
            "WHERE ur.user_id=#{userId} AND r.role_key=#{roleKey} AND r.status='ENABLED' AND r.deleted=0 " +
            "AND u.status='ENABLED' AND u.approval_status='APPROVED' AND u.deleted=0")
    int countEnabledRole(@Param("userId") Long userId, @Param("roleKey") String roleKey);
}
