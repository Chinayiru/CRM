package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增或修改菜单权限的请求参数。 */
public record SysMenuSaveRequest(
        Long parentId,

        @NotBlank(message = "菜单名称不能为空")
        @Size(max = 100, message = "菜单名称不能超过100个字符")
        String menuName,

        @NotBlank(message = "菜单类型不能为空")
        @Pattern(regexp = "DIRECTORY|MENU|BUTTON", message = "菜单类型只能是 DIRECTORY、MENU 或 BUTTON")
        String menuType,

        @Size(max = 255, message = "路由路径不能超过255个字符")
        String path,

        @Size(max = 150, message = "权限标识不能超过150个字符")
        String permissionKey,

        @Min(value = 0, message = "排序号不能小于0")
        Integer sortNo,

        @Pattern(regexp = "ENABLED|DISABLED", message = "状态只能是 ENABLED 或 DISABLED")
        String status
) {
}
