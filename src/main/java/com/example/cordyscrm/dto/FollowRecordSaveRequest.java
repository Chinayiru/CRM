package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record FollowRecordSaveRequest(
        @NotBlank(message="关联对象类型不能为空") @Pattern(regexp="CUSTOMER|OPPORTUNITY", message="关联对象类型无效") String targetType,
        @NotNull(message="关联对象ID不能为空") Long targetId,
        @Size(max=100) String method,
        @NotBlank(message="跟进内容不能为空") String content,
        LocalDateTime nextTime,
        @NotNull(message="跟进人ID不能为空") Long ownerId
) {}

