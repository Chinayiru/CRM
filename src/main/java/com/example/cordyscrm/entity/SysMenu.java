package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private Long parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String permissionKey;
    private Integer sortNo;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

