package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.OpportunitySaveRequest;
import com.example.cordyscrm.entity.Opportunity;

public interface OpportunityService extends IService<Opportunity> {
    Opportunity getOpportunity(Long id);
    Opportunity create(OpportunitySaveRequest request);
    Opportunity update(Long id, OpportunitySaveRequest request);
    void delete(Long id);
}

