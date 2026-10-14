package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank(message = "用户名不能为空") @Size(max = 64, message = "用户名不能超过64个字符") String username,
        @NotBlank(message = "密码不能为空") @Size(min = 8, max = 128, message = "密码长度必须为8到128个字符") String password,
        @Size(max = 100, message = "昵称不能超过100个字符") String nickname,
        @Size(max = 32, message = "联系电话不能超过32个字符") String phone,
        @Email(message = "邮箱格式不正确") @Size(max = 254, message = "邮箱不能超过254个字符") String email) {
}
