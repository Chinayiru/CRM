package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** 新增或修改跟进记录的请求参数。 */
public record FollowRecordSaveRequest(
        @NotBlank(message = "关联对象类型不能为空")
        @Pattern(regexp = "CUSTOMER|OPPORTUNITY", message = "关联对象类型只能是 CUSTOMER 或 OPPORTUNITY")
        String targetType,

        @NotNull(message = "关联对象ID不能为空")
        Long targetId,

        @Size(max = 100, message = "跟进方式不能超过100个字符")
        String method,

        @NotBlank(message = "跟进内容不能为空")
        @Size(max = 5000, message = "跟进内容不能超过5000个字符")
        String content,

        LocalDateTime nextTime,

        @NotNull(message = "跟进人ID不能为空")
        Long ownerId
) {
}
