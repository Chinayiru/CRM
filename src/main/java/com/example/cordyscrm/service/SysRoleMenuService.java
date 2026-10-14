package com.example.cordyscrm.service;

import com.example.cordyscrm.dto.SysRoleMenuSaveRequest;
import com.example.cordyscrm.entity.SysRoleMenu;
import java.util.List;

public interface SysRoleMenuService {
    List<SysRoleMenu> list();
    SysRoleMenu get(Long roleId,Long menuId);
    SysRoleMenu create(SysRoleMenuSaveRequest request);
    void delete(Long roleId,Long menuId);
}
