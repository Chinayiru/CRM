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
}
