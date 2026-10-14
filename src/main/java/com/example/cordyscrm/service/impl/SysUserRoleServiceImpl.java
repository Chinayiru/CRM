package com.example.cordyscrm.service.impl;

import com.example.cordyscrm.dto.SysUserRoleSaveRequest;
import com.example.cordyscrm.entity.SysUserRole;
import com.example.cordyscrm.mapper.SysUserRoleMapper;
import com.example.cordyscrm.service.SysUserRoleService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {
    @Resource private SysUserRoleMapper mapper;
    @Override public List<SysUserRole> list() { return mapper.findAll(); }
    @Override public SysUserRole get(Long userId, Long roleId) {
        SysUserRole value=mapper.find(userId,roleId);
        if(value==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"用户角色关联不存在");
        return value;
    }
    @Override @Transactional public SysUserRole create(SysUserRoleSaveRequest request) {
        if(mapper.find(request.userId(),request.roleId())!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"该用户已拥有此角色");
        mapper.insert(request.userId(),request.roleId());
        return mapper.find(request.userId(),request.roleId());
    }
    @Override @Transactional public void delete(Long userId,Long roleId) {
        if(mapper.delete(userId,roleId)==0) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"用户角色关联不存在");
    }
}
