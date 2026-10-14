package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 新增或修改线索的请求参数。 */
public record ClueSaveRequest(
        @NotBlank(message = "线索名称不能为空")
        @Size(max = 150, message = "线索名称不能超过150个字符")
        String name,

        @Size(max = 32, message = "联系电话不能超过32个字符")
        String phone,

        @Size(max = 100, message = "线索来源不能超过100个字符")
        String source,

        @NotNull(message = "负责人ID不能为空")
        Long ownerId,

        @Pattern(regexp = "NEW|FOLLOWING|CONVERTED|DISCARDED", message = "线索状态只能是 NEW、FOLLOWING、CONVERTED 或 DISCARDED")
        String status,

        @Size(max = 5000, message = "备注不能超过5000个字符")
        String remark
) {
}
