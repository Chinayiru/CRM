package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BootstrapAdminRequest(
        @NotBlank(message = "初始化密钥不能为空") String bootstrapSecret,
        @NotBlank(message = "用户名不能为空") @Size(max = 64, message = "用户名不能超过64个字符") String username,
        @NotBlank(message = "密码不能为空") @Size(min = 8, max = 128, message = "密码长度必须为8到128个字符") String password,
        @Size(max = 100, message = "昵称不能超过100个字符") String nickname) {
}
