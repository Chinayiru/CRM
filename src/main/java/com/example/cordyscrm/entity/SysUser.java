package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String phone;
    private String email;
    private Long departmentId;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
