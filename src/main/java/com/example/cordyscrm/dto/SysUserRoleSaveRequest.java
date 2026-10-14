package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotNull;

public record SysUserRoleSaveRequest(
        @NotNull(message="用户ID不能为空") Long userId,
        @NotNull(message="角色ID不能为空") Long roleId
) {}
