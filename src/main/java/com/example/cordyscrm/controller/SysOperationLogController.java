package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysOperationLogSaveRequest;
import com.example.cordyscrm.entity.SysOperationLog;
import com.example.cordyscrm.service.SysOperationLogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Immutable audit records can be appended and read, but not changed or deleted. */
@RestController
@RequestMapping("/api/operation-logs")
public class SysOperationLogController {
    @Resource private SysOperationLogService service;
    @GetMapping public List<SysOperationLog> list(){return service.list();}
    @GetMapping("/{id}") public SysOperationLog get(@PathVariable Long id){return service.getLog(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SysOperationLog create(@Valid @RequestBody SysOperationLogSaveRequest request){return service.create(request);}
}
