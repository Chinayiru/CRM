package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SysMenuSaveRequest(
        Long parentId,
        @NotBlank(message="菜单名称不能为空") @Size(max=100) String menuName,
        @NotBlank(message="菜单类型不能为空") @Pattern(regexp="DIRECTORY|MENU|BUTTON", message="菜单类型无效") String menuType,
        @Size(max=255) String path,
        @Size(max=150) String permissionKey,
        @Min(0) Integer sortNo,
        @Pattern(regexp="ENABLED|DISABLED", message="状态只能是 ENABLED 或 DISABLED") String status
) {}

