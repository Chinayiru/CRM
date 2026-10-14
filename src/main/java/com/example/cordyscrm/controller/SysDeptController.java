package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.DeptSaveRequest;
import com.example.cordyscrm.entity.SysDept;
import com.example.cordyscrm.service.SysDeptService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author cxt
 * @Date 2026-10-14 00:48
 */

@RestController
@RequestMapping("/api/departments")
public class SysDeptController {

    @Resource
    private SysDeptService deptService;

    @GetMapping
    public ApiResponse<List<SysDept>> list() {
        return ApiResponse.success(deptService.list());
    }

    @PostMapping
    public ApiResponse<SysDept> create(@Valid @RequestBody DeptSaveRequest request) {
        return ApiResponse.success(deptService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<SysDept> update(
            @PathVariable Long id,
            @Valid @RequestBody DeptSaveRequest request
    ) {
        return ApiResponse.success(deptService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return ApiResponse.success(null);
    }
}
