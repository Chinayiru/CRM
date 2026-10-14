package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysDictSaveRequest;
import com.example.cordyscrm.entity.SysDict;
import com.example.cordyscrm.service.SysDictService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dicts")
public class SysDictController {
    @Resource private SysDictService service;
    @GetMapping public ApiResponse<List<SysDict>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<SysDict> get(@PathVariable Long id) { return ApiResponse.success(service.getSysDict(id)); }
    @PostMapping
    public ApiResponse<SysDict> create(@Valid @RequestBody SysDictSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<SysDict> update(@PathVariable Long id, @Valid @RequestBody SysDictSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

