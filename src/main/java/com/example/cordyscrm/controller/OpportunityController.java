package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.OpportunitySaveRequest;
import com.example.cordyscrm.entity.Opportunity;
import com.example.cordyscrm.service.OpportunityService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    @Resource private OpportunityService service;
    @GetMapping public ApiResponse<List<Opportunity>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<Opportunity> get(@PathVariable Long id) { return ApiResponse.success(service.getOpportunity(id)); }
    @PostMapping
    public ApiResponse<Opportunity> create(@Valid @RequestBody OpportunitySaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<Opportunity> update(@PathVariable Long id, @Valid @RequestBody OpportunitySaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

