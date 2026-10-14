package com.example.cordyscrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 新增或修改系统用户的请求参数。
 *
 * <p>新增时 {@code password} 必填（长度下限由服务层校验为 8 位），修改时留空表示不改密码。
 */
public record SysUserSaveRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(max = 64, message = "用户名不能超过64个字符")
        String username,

        @Size(max = 128, message = "密码不能超过128个字符")
        String password,

        @Size(max = 100, message = "昵称不能超过100个字符")
        String nickname,

        @Size(max = 32, message = "联系电话不能超过32个字符")
        String phone,

        @Email(message = "邮箱格式不正确")
        @Size(max = 254, message = "邮箱不能超过254个字符")
        String email,

        Long departmentId,

        @Pattern(regexp = "ENABLED|DISABLED", message = "状态只能是 ENABLED 或 DISABLED")
        String status
) {
}
