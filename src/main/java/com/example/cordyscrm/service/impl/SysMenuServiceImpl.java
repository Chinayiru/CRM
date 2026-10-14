package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.SysMenuSaveRequest;
import com.example.cordyscrm.entity.SysMenu;
import com.example.cordyscrm.mapper.SysMenuMapper;
import com.example.cordyscrm.service.SysMenuService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Override public List<SysMenu> list() { return lambdaQuery().orderByDesc(SysMenu::getId).list(); }
    @Override public SysMenu getSysMenu(Long id) { return active(id); }
    @Override @Transactional public SysMenu create(SysMenuSaveRequest request) {
        SysMenu entity = new SysMenu();
        apply(entity, request);
        if (entity.getSortNo() == null) entity.setSortNo(0);
        if (entity.getStatus() == null) entity.setStatus("ENABLED");
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public SysMenu update(Long id, SysMenuSaveRequest request) {
        SysMenu entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private SysMenu active(Long id) {
        SysMenu entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "菜单不存在");
        return entity;
    }
    private void apply(SysMenu entity, SysMenuSaveRequest request) {
        if (request.parentId() != null) entity.setParentId(request.parentId());
        if (request.menuName() != null) entity.setMenuName(trimToNull(request.menuName()));
        if (request.menuType() != null) entity.setMenuType(trimToNull(request.menuType()));
        if (request.path() != null) entity.setPath(trimToNull(request.path()));
        if (request.permissionKey() != null) entity.setPermissionKey(trimToNull(request.permissionKey()));
        if (request.sortNo() != null) entity.setSortNo(request.sortNo());
        if (request.status() != null) entity.setStatus(trimToNull(request.status()));
    }
    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

