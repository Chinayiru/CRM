package com.example.cordyscrm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractSaveRequest(
        @NotBlank(message="合同编号不能为空") @Size(max=64) String contractNo,
        @NotBlank(message="合同名称不能为空") @Size(max=200) String name,
        @NotNull(message="客户ID不能为空") Long customerId,
        Long opportunityId,
        @DecimalMin(value="0.00", message="合同金额不能小于0") BigDecimal amount,
        LocalDate signDate,
        LocalDate expireDate,
        @Pattern(regexp="DRAFT|EFFECTIVE|IN_PROGRESS|COMPLETED|TERMINATED", message="合同状态无效") String status,
        @Size(max=1000) String terminateReason
) {}

