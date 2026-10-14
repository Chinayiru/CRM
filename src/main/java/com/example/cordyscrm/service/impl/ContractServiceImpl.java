package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.ContractSaveRequest;
import com.example.cordyscrm.entity.Contract;
import com.example.cordyscrm.mapper.ContractMapper;
import com.example.cordyscrm.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {
    @Override public List<Contract> list() { return lambdaQuery().orderByDesc(Contract::getId).list(); }
    @Override public Contract getContract(Long id) { return active(id); }
    @Override @Transactional public Contract create(ContractSaveRequest request) {
        validate(request);
        Contract entity = new Contract();
        apply(entity, request);
        if (entity.getAmount() == null) entity.setAmount(java.math.BigDecimal.ZERO);
        if (entity.getStatus() == null) entity.setStatus(DRAFT);
        entity.setDeleted(0);
        save(entity);
        return entity;
    }
    @Override @Transactional public Contract update(Long id, ContractSaveRequest request) {
        validate(request);
        Contract entity = active(id);
        apply(entity, request);
        updateById(entity);
        return entity;
    }
    @Override @Transactional public void delete(Long id) { active(id); removeById(id); }
    private Contract active(Long id) {
        Contract entity = getById(id);
        if (entity == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "合同不存在");
        return entity;
    }
    private void apply(Contract entity, ContractSaveRequest request) {
        if (request.contractNo() != null) entity.setContractNo(trimToNull(request.contractNo()));
        if (request.name() != null) entity.setName(trimToNull(request.name()));
        if (request.customerId() != null) entity.setCustomerId(request.customerId());
        if (request.opportunityId() != null) entity.setOpportunityId(request.opportunityId());
        if (request.amount() != null) entity.setAmount(request.amount());
        if (request.signDate() != null) entity.setSignDate(request.signDate());
        if (request.expireDate() != null) entity.setExpireDate(request.expireDate());
        if (request.status() != null) entity.setStatus(trimToNull(request.status()));
        if (request.terminateReason() != null) entity.setTerminateReason(trimToNull(request.terminateReason()));
    }
    private void validate(ContractSaveRequest r) {
        if (r.status() != null && !"DRAFT".equals(r.status()) && r.signDate() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "非草稿合同必须填写签订日期");
        if ("TERMINATED".equals(r.status()) && (r.terminateReason() == null || r.terminateReason().isBlank())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "终止合同时必须填写终止原因");
    }
    private String trimToNull(String value) { if (value == null) return null; String v=value.trim(); return v.isEmpty()?null:v; }
}

