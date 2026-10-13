package com.example.cordyscrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author cxt
 * @Date 2026-10-14 00:26
 */

//部门表
@TableName("sys_dept")
@Data
public class SysDept {

    /** 部门主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 上级部门 ID；根部门为空 */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 排序号 */
    private Integer sortNo;

    /** 状态：ENABLED、DISABLED */
    private String status;

    /** 创建人 ID */
    private Long createdBy;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新人 ID */
    private Long updatedBy;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 逻辑删除标记：0 未删除，1 已删除 */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

}
