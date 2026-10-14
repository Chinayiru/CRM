package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.FollowRecordSaveRequest;
import com.example.cordyscrm.entity.FollowRecord;

public interface FollowRecordService extends IService<FollowRecord> {
    FollowRecord getFollowRecord(Long id);
    FollowRecord create(FollowRecordSaveRequest request);
    FollowRecord update(Long id, FollowRecordSaveRequest request);
    void delete(Long id);
}

