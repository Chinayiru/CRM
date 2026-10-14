package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotNull;

public record SysRoleMenuSaveRequest(
        @NotNull(message="角色ID不能为空") Long roleId,
        @NotNull(message="菜单ID不能为空") Long menuId
) {}
