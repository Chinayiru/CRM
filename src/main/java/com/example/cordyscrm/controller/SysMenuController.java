package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysMenuSaveRequest;
import com.example.cordyscrm.entity.SysMenu;
import com.example.cordyscrm.service.SysMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class SysMenuController {
    @Resource private SysMenuService service;
    @GetMapping public ApiResponse<List<SysMenu>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<SysMenu> get(@PathVariable Long id) { return ApiResponse.success(service.getSysMenu(id)); }
    @PostMapping
    public ApiResponse<SysMenu> create(@Valid @RequestBody SysMenuSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<SysMenu> update(@PathVariable Long id, @Valid @RequestBody SysMenuSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

