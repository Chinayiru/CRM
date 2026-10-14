package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.OpportunitySaveRequest;
import com.example.cordyscrm.entity.Opportunity;
import com.example.cordyscrm.mapper.OpportunityMapper;
import com.example.cordyscrm.service.OpportunityService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements OpportunityService {
    @Override public List<Opportunity> list() { return lambdaQuery().orderByDesc(Opportunity::getId).list(); }
    @Override public Opportunity getOpportunity(Long id) { return active(id); }
    @Override @Transactional public Opportunity create(OpportunitySaveRequest request) {
        validate(request);
        Opportunity entity = new Opportunity();
        apply(entity, request);
        if (entity.getAmount() == null) entity.setAmount(java.math.BigDecimal.ZERO);
        if (entity.getStage() == null) entity.setStage(INITIAL_CONTACT);
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public Opportunity update(Long id, OpportunitySaveRequest request) {
        validate(request);
        Opportunity entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private Opportunity active(Long id) {
        Opportunity entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "商机不存在");
        return entity;
    }
    private void apply(Opportunity entity, OpportunitySaveRequest request) {
        if (request.name() != null) entity.setName(trimToNull(request.name()));
        if (request.customerId() != null) entity.setCustomerId(request.customerId());
        if (request.amount() != null) entity.setAmount(request.amount());
        if (request.stage() != null) entity.setStage(trimToNull(request.stage()));
        if (request.expectTime() != null) entity.setExpectTime(request.expectTime());
        if (request.winRate() != null) entity.setWinRate(request.winRate());
        if (request.lostReason() != null) entity.setLostReason(trimToNull(request.lostReason()));
        if (request.closedAt() != null) entity.setClosedAt(request.closedAt());
        if (request.ownerId() != null) entity.setOwnerId(request.ownerId());
    }
    private void validate(OpportunitySaveRequest r) {
        if ("LOST".equals(r.stage()) && (r.lostReason() == null || r.lostReason().isBlank())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "输单时必须填写输单原因");
    }
    private String trimToNull(String value) { if (value == null) return null; String v=value.trim(); return v.isEmpty()?null:v; }
}

