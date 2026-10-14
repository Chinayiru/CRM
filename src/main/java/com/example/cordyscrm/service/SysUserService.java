package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import com.example.cordyscrm.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getUser(Long id);
    SysUser create(SysUserSaveRequest request);
    SysUser update(Long id, SysUserSaveRequest request);
    void delete(Long id);
}
