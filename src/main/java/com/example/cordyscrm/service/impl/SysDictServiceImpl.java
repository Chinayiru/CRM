package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.SysDictSaveRequest;
import com.example.cordyscrm.entity.SysDict;
import com.example.cordyscrm.mapper.SysDictMapper;
import com.example.cordyscrm.service.SysDictService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {
    @Override public List<SysDict> list() { return lambdaQuery().orderByDesc(SysDict::getId).list(); }
    @Override public SysDict getSysDict(Long id) { return active(id); }
    @Override @Transactional public SysDict create(SysDictSaveRequest request) {
        SysDict entity = new SysDict();
        apply(entity, request);
        if (entity.getSortNo() == null) entity.setSortNo(0);
        if (entity.getStatus() == null) entity.setStatus(ENABLED);
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public SysDict update(Long id, SysDictSaveRequest request) {
        SysDict entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private SysDict active(Long id) {
        SysDict entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "字典不存在");
        return entity;
    }
    private void apply(SysDict entity, SysDictSaveRequest request) {
        if (request.dictType() != null) entity.setDictType(trimToNull(request.dictType()));
        if (request.dictLabel() != null) entity.setDictLabel(trimToNull(request.dictLabel()));
        if (request.dictValue() != null) entity.setDictValue(trimToNull(request.dictValue()));
        if (request.sortNo() != null) entity.setSortNo(request.sortNo());
        if (request.status() != null) entity.setStatus(trimToNull(request.status()));
    }
    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

