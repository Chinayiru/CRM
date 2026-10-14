package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("clue")
public class Clue {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private String name;
    private String phone;
    private String source;
    private Long ownerId;
    private String status;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

