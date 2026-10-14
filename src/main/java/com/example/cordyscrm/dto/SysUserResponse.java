package com.example.cordyscrm.dto;

import com.example.cordyscrm.entity.SysUser;
import java.time.LocalDateTime;

public record SysUserResponse(Long id, String username, String nickname, String phone,
                              String email, Long departmentId, String status, String approvalStatus,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static SysUserResponse from(SysUser user) {
        return new SysUserResponse(user.getId(), user.getUsername(), user.getNickname(),
                user.getPhone(), user.getEmail(), user.getDepartmentId(), user.getStatus(), user.getApprovalStatus(),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
