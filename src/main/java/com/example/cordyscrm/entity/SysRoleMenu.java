package com.example.cordyscrm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysRoleMenu {
    private Long roleId;
    private Long menuId;
    private LocalDateTime createdAt;
}
