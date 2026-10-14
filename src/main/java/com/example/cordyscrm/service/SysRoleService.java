package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.SysRoleSaveRequest;
import com.example.cordyscrm.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {
    SysRole getSysRole(Long id);
    SysRole create(SysRoleSaveRequest request);
    SysRole update(Long id, SysRoleSaveRequest request);
    void delete(Long id);
}

