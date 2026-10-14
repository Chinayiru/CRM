package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SysRoleSaveRequest(
        @NotBlank(message="角色名称不能为空") @Size(max=100) String roleName,
        @NotBlank(message="角色标识不能为空") @Size(max=64) String roleKey,
        @NotBlank(message="数据范围不能为空") @Pattern(regexp="SELF|DEPT|ALL", message="数据范围只能是 SELF、DEPT 或 ALL") String dataScope,
        @Size(max=500) String remark,
        @Pattern(regexp="ENABLED|DISABLED", message="状态只能是 ENABLED 或 DISABLED") String status
) {}

