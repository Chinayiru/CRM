package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增或修改字典项的请求参数。 */
public record SysDictSaveRequest(
        @NotBlank(message = "字典类型不能为空")
        @Size(max = 64, message = "字典类型不能超过64个字符")
        String dictType,

        @NotBlank(message = "字典名称不能为空")
        @Size(max = 100, message = "字典名称不能超过100个字符")
        String dictLabel,

        @NotBlank(message = "字典值不能为空")
        @Size(max = 100, message = "字典值不能超过100个字符")
        String dictValue,

        @Min(value = 0, message = "排序号不能小于0")
        Integer sortNo,

        @Pattern(regexp = "ENABLED|DISABLED", message = "状态只能是 ENABLED 或 DISABLED")
        String status
) {
}
