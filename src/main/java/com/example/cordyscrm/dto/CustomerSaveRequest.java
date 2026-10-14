package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增或修改客户的请求参数。 */
public record CustomerSaveRequest(
        @NotBlank(message = "客户名称不能为空")
        @Size(max = 200, message = "客户名称不能超过200个字符")
        String name,

        @Size(max = 100, message = "客户类型不能超过100个字符")
        String customerType,

        @Size(max = 100, message = "联系人不能超过100个字符")
        String contact,

        @Size(max = 32, message = "联系电话不能超过32个字符")
        String phone,

        @Size(max = 100, message = "行业不能超过100个字符")
        String industry,

        @NotNull(message = "负责人ID不能为空")
        Long ownerId,

        @Size(max = 100, message = "客户来源不能超过100个字符")
        String source,

        @Size(max = 500, message = "地址不能超过500个字符")
        String address,

        @Pattern(regexp = "ACTIVE|INACTIVE", message = "客户状态只能是 ACTIVE 或 INACTIVE")
        String status,

        Long sourceClueId
) {
}
