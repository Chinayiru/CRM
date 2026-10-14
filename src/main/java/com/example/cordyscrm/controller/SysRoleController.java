package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysRoleSaveRequest;
import com.example.cordyscrm.entity.SysRole;
import com.example.cordyscrm.service.SysRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class SysRoleController {
    @Resource private SysRoleService service;
    @GetMapping public ApiResponse<List<SysRole>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<SysRole> get(@PathVariable Long id) { return ApiResponse.success(service.getSysRole(id)); }
    @PostMapping
    public ApiResponse<SysRole> create(@Valid @RequestBody SysRoleSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<SysRole> update(@PathVariable Long id, @Valid @RequestBody SysRoleSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

