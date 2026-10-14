package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.SysOperationLogSaveRequest;
import com.example.cordyscrm.entity.SysOperationLog;
import com.example.cordyscrm.mapper.SysOperationLogMapper;
import com.example.cordyscrm.service.SysOperationLogService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {
    @Override public List<SysOperationLog> list(){return lambdaQuery().orderByDesc(SysOperationLog::getCreatedAt).orderByDesc(SysOperationLog::getId).list();}
    @Override public SysOperationLog getLog(Long id){
        SysOperationLog log=getById(id);
        if(log==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"操作日志不存在");
        return log;
    }
    @Override @Transactional public SysOperationLog create(SysOperationLogSaveRequest request){
        if(request.durationMs()!=null && request.durationMs()<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"耗时不能小于0");
        SysOperationLog log=new SysOperationLog();
        log.setOperatorId(request.operatorId());
        log.setModule(request.module().trim());
        log.setAction(request.action().trim());
        log.setTargetType(trimToNull(request.targetType()));
        log.setTargetId(trimToNull(request.targetId()));
        log.setResult(request.result());
        log.setIpAddress(trimToNull(request.ipAddress()));
        log.setDetail(trimToNull(request.detail()));
        log.setDurationMs(request.durationMs());
        save(log);
        return log;
    }
    private String trimToNull(String value){if(value==null)return null;String v=value.trim();return v.isEmpty()?null:v;}
}
