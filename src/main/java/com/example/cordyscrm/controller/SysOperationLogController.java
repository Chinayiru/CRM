package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysOperationLogSaveRequest;
import com.example.cordyscrm.entity.SysOperationLog;
import com.example.cordyscrm.service.SysOperationLogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Immutable audit records can be appended and read, but not changed or deleted. */
@RestController
@RequestMapping("/api/operation-logs")
public class SysOperationLogController {
    @Resource private SysOperationLogService service;
    @GetMapping public ApiResponse<List<SysOperationLog>> list(){return ApiResponse.success(service.list());}
    @GetMapping("/{id}") public ApiResponse<SysOperationLog> get(@PathVariable Long id){return ApiResponse.success(service.getLog(id));}
    @PostMapping
    public ApiResponse<SysOperationLog> create(@Valid @RequestBody SysOperationLogSaveRequest request){return ApiResponse.success(service.create(request));}
}
