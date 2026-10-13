package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @Author cxt
 * @Date 2026-10-14 00:39
 */
public record DeptSaveRequest(
        @NotBlank(message = "部门名称不能为空")
        @Size(max = 100, message = "部门名称不能超过100个字符")
        String deptName,

        Long parentId,

        @Min(value = 0, message = "排序号不能小于0")
        Integer sortNo,

        @Pattern(
                regexp = "ENABLED|DISABLED",
                message = "状态只能是 ENABLED 或 DISABLED"
        )
        String status
) {

}
