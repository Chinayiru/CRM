package com.example.cordyscrm.mapper;

import com.example.cordyscrm.entity.SysRoleMenu;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysRoleMenuMapper {
    @Select("SELECT role_id AS roleId, menu_id AS menuId, created_at AS createdAt FROM sys_role_menu ORDER BY created_at DESC")
    List<SysRoleMenu> findAll();
    @Select("SELECT role_id AS roleId, menu_id AS menuId, created_at AS createdAt FROM sys_role_menu WHERE role_id=#{roleId} AND menu_id=#{menuId}")
    SysRoleMenu find(@Param("roleId") Long roleId,@Param("menuId") Long menuId);
    @Insert("INSERT INTO sys_role_menu(role_id,menu_id) VALUES(#{roleId},#{menuId})")
    int insert(@Param("roleId") Long roleId,@Param("menuId") Long menuId);
    @Delete("DELETE FROM sys_role_menu WHERE role_id=#{roleId} AND menu_id=#{menuId}")
    int delete(@Param("roleId") Long roleId,@Param("menuId") Long menuId);
}
