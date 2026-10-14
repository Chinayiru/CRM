package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("follow_record")
public class FollowRecord {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private String targetType;
    private Long targetId;
    private String method;
    private String content;
    private LocalDateTime nextTime;
    private Long ownerId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

