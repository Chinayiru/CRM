package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("contract")
public class Contract {
    @TableId(value="id", type=IdType.AUTO)
    private Long id;
    private String contractNo;
    private String name;
    private Long customerId;
    private Long opportunityId;
    private BigDecimal amount;
    private LocalDate signDate;
    private LocalDate expireDate;
    private String status;
    private String terminateReason;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value="0", delval="1")
    private Integer deleted;
}

