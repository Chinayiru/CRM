package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增或修改角色的请求参数。 */
public record SysRoleSaveRequest(
        @NotBlank(message = "角色名称不能为空")
        @Size(max = 100, message = "角色名称不能超过100个字符")
        String roleName,

        @NotBlank(message = "角色标识不能为空")
        @Size(max = 64, message = "角色标识不能超过64个字符")
        String roleKey,

        @NotBlank(message = "数据范围不能为空")
        @Pattern(regexp = "SELF|DEPT|ALL", message = "数据范围只能是 SELF、DEPT 或 ALL")
        String dataScope,

        @Size(max = 500, message = "备注不能超过500个字符")
        String remark,

        @Pattern(regexp = "ENABLED|DISABLED", message = "状态只能是 ENABLED 或 DISABLED")
        String status
) {
}
