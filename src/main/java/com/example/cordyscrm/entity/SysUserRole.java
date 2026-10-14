package com.example.cordyscrm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUserRole {
    private Long userId;
    private Long roleId;
    private LocalDateTime createdAt;
}
