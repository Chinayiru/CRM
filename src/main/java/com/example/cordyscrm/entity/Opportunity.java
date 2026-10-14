package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("opportunity")
public class Opportunity {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private String name;
    private Long customerId;
    private BigDecimal amount;
    private String stage;
    private LocalDate expectTime;
    private Integer winRate;
    private String lostReason;
    private LocalDateTime closedAt;
    private Long ownerId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

