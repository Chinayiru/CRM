package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.SysRoleSaveRequest;
import com.example.cordyscrm.entity.SysRole;
import com.example.cordyscrm.mapper.SysRoleMapper;
import com.example.cordyscrm.service.SysRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Override public List<SysRole> list() { return lambdaQuery().orderByDesc(SysRole::getId).list(); }
    @Override public SysRole getSysRole(Long id) { return active(id); }
    @Override @Transactional public SysRole create(SysRoleSaveRequest request) {
        SysRole entity = new SysRole();
        apply(entity, request);
        if (entity.getDataScope() == null) entity.setDataScope(SELF);
        if (entity.getStatus() == null) entity.setStatus(ENABLED);
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public SysRole update(Long id, SysRoleSaveRequest request) {
        SysRole entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private SysRole active(Long id) {
        SysRole entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "角色不存在");
        return entity;
    }
    private void apply(SysRole entity, SysRoleSaveRequest request) {
        if (request.roleName() != null) entity.setRoleName(trimToNull(request.roleName()));
        if (request.roleKey() != null) entity.setRoleKey(trimToNull(request.roleKey()));
        if (request.dataScope() != null) entity.setDataScope(trimToNull(request.dataScope()));
        if (request.remark() != null) entity.setRemark(trimToNull(request.remark()));
        if (request.status() != null) entity.setStatus(trimToNull(request.status()));
    }
    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

