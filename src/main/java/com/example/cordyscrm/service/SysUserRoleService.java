package com.example.cordyscrm.service;

import com.example.cordyscrm.dto.SysUserRoleSaveRequest;
import com.example.cordyscrm.entity.SysUserRole;
import java.util.List;

public interface SysUserRoleService {
    List<SysUserRole> list();
    SysUserRole get(Long userId, Long roleId);
    SysUserRole create(SysUserRoleSaveRequest request);
    void delete(Long userId, Long roleId);
}
