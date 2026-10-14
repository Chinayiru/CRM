package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SysOperationLogSaveRequest(
        Long operatorId,
        @NotBlank(message="模块名称不能为空") @Size(max=100) String module,
        @NotBlank(message="操作名称不能为空") @Size(max=100) String action,
        @Size(max=100) String targetType,
        @Size(max=100) String targetId,
        @NotBlank(message="操作结果不能为空") @Pattern(regexp="SUCCESS|FAILURE",message="操作结果只能是 SUCCESS 或 FAILURE") String result,
        @Size(max=45) String ipAddress,
        String detail,
        Long durationMs
) {}
