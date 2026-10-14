package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(value="id",type=IdType.AUTO)
    private Long id;
    private Long operatorId;
    private String module;
    private String action;
    private String targetType;
    private String targetId;
    private String result;
    private String ipAddress;
    /** JSON text; the database JSON column validates its syntax. */
    private String detail;
    private Long durationMs;
    private LocalDateTime createdAt;
}
