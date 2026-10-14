package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.FollowRecordSaveRequest;
import com.example.cordyscrm.entity.FollowRecord;
import com.example.cordyscrm.mapper.FollowRecordMapper;
import com.example.cordyscrm.service.FollowRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class FollowRecordServiceImpl extends ServiceImpl<FollowRecordMapper, FollowRecord> implements FollowRecordService {
    @Override public List<FollowRecord> list() { return lambdaQuery().orderByDesc(FollowRecord::getId).list(); }
    @Override public FollowRecord getFollowRecord(Long id) { return active(id); }
    @Override @Transactional public FollowRecord create(FollowRecordSaveRequest request) {
        FollowRecord entity = new FollowRecord();
        apply(entity, request);

        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public FollowRecord update(Long id, FollowRecordSaveRequest request) {
        FollowRecord entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private FollowRecord active(Long id) {
        FollowRecord entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "跟进记录不存在");
        return entity;
    }
    private void apply(FollowRecord entity, FollowRecordSaveRequest request) {
        if (request.targetType() != null) entity.setTargetType(trimToNull(request.targetType()));
        if (request.targetId() != null) entity.setTargetId(request.targetId());
        if (request.method() != null) entity.setMethod(trimToNull(request.method()));
        if (request.content() != null) entity.setContent(trimToNull(request.content()));
        if (request.nextTime() != null) entity.setNextTime(request.nextTime());
        if (request.ownerId() != null) entity.setOwnerId(request.ownerId());
    }
    private String trimToNull(String value) { if (value == null) return null; String v=value.trim(); return v.isEmpty()?null:v; }
}

