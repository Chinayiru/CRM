package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.FollowRecordSaveRequest;
import com.example.cordyscrm.entity.FollowRecord;
import com.example.cordyscrm.service.FollowRecordService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/follow-records")
public class FollowRecordController {
    @Resource private FollowRecordService service;
    @GetMapping public ApiResponse<List<FollowRecord>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<FollowRecord> get(@PathVariable Long id) { return ApiResponse.success(service.getFollowRecord(id)); }
    @PostMapping
    public ApiResponse<FollowRecord> create(@Valid @RequestBody FollowRecordSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<FollowRecord> update(@PathVariable Long id, @Valid @RequestBody FollowRecordSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

