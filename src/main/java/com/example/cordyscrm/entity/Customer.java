package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 客户档案。 */
@Data
@TableName("customer")
public class Customer {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;
    private String customerType;
    private String contact;
    private String phone;
    private String industry;
    private Long ownerId;
    private String source;
    private String address;
    private String status;
    private Long sourceClueId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
