package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private String roleName;
    private String roleKey;
    private String dataScope;
    private String remark;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

