package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.SysUserResponse;
import com.example.cordyscrm.security.AdminAccessService;
import com.example.cordyscrm.security.AuthInterceptor;
import com.example.cordyscrm.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/registrations")
@Tag(name = "用户注册审核")
public class AdminRegistrationController {
    private final SysUserService userService;
    private final AdminAccessService adminAccessService;

    public AdminRegistrationController(SysUserService userService, AdminAccessService adminAccessService) {
        this.userService = userService;
        this.adminAccessService = adminAccessService;
    }

    @GetMapping
    @Operation(summary = "查询待审核的注册申请")
    public ApiResponse<List<SysUserResponse>> listPending(
            @RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ID) Long operatorId) {
        adminAccessService.requireAdministrator(operatorId);
        return ApiResponse.success(userService.listPendingRegistrations().stream().map(SysUserResponse::from).toList());
    }

    @PostMapping("/{userId}/approve")
    @Operation(summary = "通过用户注册申请")
    public ApiResponse<SysUserResponse> approve(
            @PathVariable Long userId,
            @RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ID) Long operatorId) {
        adminAccessService.requireAdministrator(operatorId);
        return ApiResponse.success("已通过注册申请", SysUserResponse.from(userService.approveRegistration(userId, operatorId)));
    }

    @PostMapping("/{userId}/reject")
    @Operation(summary = "拒绝用户注册申请")
    public ApiResponse<SysUserResponse> reject(
            @PathVariable Long userId,
            @RequestAttribute(AuthInterceptor.AUTHENTICATED_USER_ID) Long operatorId) {
        adminAccessService.requireAdministrator(operatorId);
        return ApiResponse.success("已拒绝注册申请", SysUserResponse.from(userService.rejectRegistration(userId, operatorId)));
    }
}
