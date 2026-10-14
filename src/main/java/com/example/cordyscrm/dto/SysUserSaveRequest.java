package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SysUserSaveRequest(
        @NotBlank(message = "用户名不能为空") @Size(max = 64) String username,
        @Size(max = 128, message = "密码不能超过128个字符") String password,
        @Size(max = 100) String nickname,
        @Size(max = 32) String phone,
        @Email(message = "邮箱格式不正确") @Size(max = 254) String email,
        Long departmentId,
        @Pattern(regexp = "ENABLED|DISABLED", message = "状态只能是 ENABLED 或 DISABLED") String status
) {}
