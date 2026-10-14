package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.ClueSaveRequest;
import com.example.cordyscrm.entity.Clue;
import com.example.cordyscrm.mapper.ClueMapper;
import com.example.cordyscrm.service.ClueService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements ClueService {
    @Override public List<Clue> list() { return lambdaQuery().orderByDesc(Clue::getId).list(); }
    @Override public Clue getClue(Long id) { return active(id); }
    @Override @Transactional public Clue create(ClueSaveRequest request) {
        Clue entity = new Clue();
        apply(entity, request);
        if (entity.getStatus() == null) entity.setStatus("NEW");
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public Clue update(Long id, ClueSaveRequest request) {
        Clue entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private Clue active(Long id) {
        Clue entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "线索不存在");
        return entity;
    }
    private void apply(Clue entity, ClueSaveRequest request) {
        if (request.name() != null) entity.setName(trimToNull(request.name()));
        if (request.phone() != null) entity.setPhone(trimToNull(request.phone()));
        if (request.source() != null) entity.setSource(trimToNull(request.source()));
        if (request.ownerId() != null) entity.setOwnerId(request.ownerId());
        if (request.status() != null) entity.setStatus(trimToNull(request.status()));
        if (request.remark() != null) entity.setRemark(trimToNull(request.remark()));
    }
    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

