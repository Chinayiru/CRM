package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysUserResponse;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import com.example.cordyscrm.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class SysUserController {
    @Resource private SysUserService userService;

    @GetMapping
    public ApiResponse<List<SysUserResponse>> list() {
        return ApiResponse.success(userService.list().stream().map(SysUserResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<SysUserResponse> get(@PathVariable Long id) {
        return ApiResponse.success(SysUserResponse.from(userService.getUser(id)));
    }

    @PostMapping
    public ApiResponse<SysUserResponse> create(@Valid @RequestBody SysUserSaveRequest request) {
        return ApiResponse.success(SysUserResponse.from(userService.create(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<SysUserResponse> update(@PathVariable Long id, @Valid @RequestBody SysUserSaveRequest request) {
        return ApiResponse.success(SysUserResponse.from(userService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { userService.delete(id);
        return ApiResponse.success(null);
    }
}
