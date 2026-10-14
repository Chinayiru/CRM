package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.SysOperationLogSaveRequest;
import com.example.cordyscrm.entity.SysOperationLog;

public interface SysOperationLogService extends IService<SysOperationLog> {
    SysOperationLog getLog(Long id);
    SysOperationLog create(SysOperationLogSaveRequest request);
}
