package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysRoleMenuSaveRequest;
import com.example.cordyscrm.entity.SysRoleMenu;
import com.example.cordyscrm.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role-menus")
public class SysRoleMenuController {
    @Resource private SysRoleMenuService service;
    @GetMapping public ApiResponse<List<SysRoleMenu>> list(){return ApiResponse.success(service.list());}
    @GetMapping("/{roleId}/{menuId}") public ApiResponse<SysRoleMenu> get(@PathVariable Long roleId,@PathVariable Long menuId){return ApiResponse.success(service.get(roleId,menuId));}
    @PostMapping public ApiResponse<SysRoleMenu> create(@Valid @RequestBody SysRoleMenuSaveRequest request){return ApiResponse.success(service.create(request));}
    @DeleteMapping("/{roleId}/{menuId}") public ApiResponse<Void> delete(@PathVariable Long roleId,@PathVariable Long menuId){service.delete(roleId,menuId);
        return ApiResponse.success(null);
    }
}
