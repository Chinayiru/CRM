package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增操作日志的请求参数。 */
public record SysOperationLogSaveRequest(
        Long operatorId,

        @NotBlank(message = "模块名称不能为空")
        @Size(max = 100, message = "模块名称不能超过100个字符")
        String module,

        @NotBlank(message = "操作名称不能为空")
        @Size(max = 100, message = "操作名称不能超过100个字符")
        String action,

        @Size(max = 100, message = "目标对象类型不能超过100个字符")
        String targetType,

        @Size(max = 100, message = "目标对象标识不能超过100个字符")
        String targetId,

        @NotBlank(message = "操作结果不能为空")
        @Pattern(regexp = "SUCCESS|FAILURE", message = "操作结果只能是 SUCCESS 或 FAILURE")
        String result,

        @Size(max = 45, message = "IP 地址不能超过45个字符")
        String ipAddress,

        @Size(max = 5000, message = "操作摘要不能超过5000个字符")
        String detail,

        @Min(value = 0, message = "耗时不能小于0")
        Long durationMs
) {
}
