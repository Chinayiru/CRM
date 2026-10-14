package com.example.cordyscrm.security;

import com.example.cordyscrm.mapper.SysUserRoleMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminAccessService {
    private final SysUserRoleMapper userRoleMapper;

    public AdminAccessService(SysUserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    public void requireAdministrator(Long userId) {
        if (userId == null || userRoleMapper.countEnabledRole(userId, "admin") == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "只有系统管理员可以审核注册申请");
        }
    }
}
