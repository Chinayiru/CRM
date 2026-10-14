package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysUserRoleSaveRequest;
import com.example.cordyscrm.entity.SysUserRole;
import com.example.cordyscrm.service.SysUserRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class SysUserRoleController {
    @Resource private SysUserRoleService service;
    @GetMapping public ApiResponse<List<SysUserRole>> list(){return ApiResponse.success(service.list());}
    @GetMapping("/{userId}/{roleId}") public ApiResponse<SysUserRole> get(@PathVariable Long userId,@PathVariable Long roleId){return ApiResponse.success(service.get(userId,roleId));}
    @PostMapping public ApiResponse<SysUserRole> create(@Valid @RequestBody SysUserRoleSaveRequest request){return ApiResponse.success(service.create(request));}
    @DeleteMapping("/{userId}/{roleId}") public ApiResponse<Void> delete(@PathVariable Long userId,@PathVariable Long roleId){service.delete(userId,roleId);
        return ApiResponse.success(null);
    }
}
