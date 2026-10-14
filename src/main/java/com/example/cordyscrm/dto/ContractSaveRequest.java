package com.example.cordyscrm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 新增或修改合同的请求参数。 */
public record ContractSaveRequest(
        @NotBlank(message = "合同编号不能为空")
        @Size(max = 64, message = "合同编号不能超过64个字符")
        String contractNo,

        @NotBlank(message = "合同名称不能为空")
        @Size(max = 200, message = "合同名称不能超过200个字符")
        String name,

        @NotNull(message = "客户ID不能为空")
        Long customerId,

        Long opportunityId,

        @DecimalMin(value = "0.00", message = "合同金额不能小于0")
        @Digits(integer = 16, fraction = 2, message = "合同金额最多16位整数、2位小数")
        BigDecimal amount,

        LocalDate signDate,

        LocalDate expireDate,

        @Pattern(regexp = "DRAFT|EFFECTIVE|IN_PROGRESS|COMPLETED|TERMINATED",
                message = "合同状态只能是 DRAFT、EFFECTIVE、IN_PROGRESS、COMPLETED 或 TERMINATED")
        String status,

        @Size(max = 1000, message = "终止原因不能超过1000个字符")
        String terminateReason
) {
}
