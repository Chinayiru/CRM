package com.example.cordyscrm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpportunitySaveRequest(
        @NotBlank(message="商机名称不能为空") @Size(max=200) String name,
        @NotNull(message="客户ID不能为空") Long customerId,
        @DecimalMin(value="0.00", message="预计金额不能小于0") BigDecimal amount,
        @Pattern(regexp="INITIAL_CONTACT|NEEDS_CONFIRMED|PROPOSAL|NEGOTIATION|WON|LOST", message="商机阶段无效") String stage,
        LocalDate expectTime,
        @Min(0) @Max(100) Integer winRate,
        @Size(max=1000) String lostReason,
        LocalDateTime closedAt,
        @NotNull(message="负责人ID不能为空") Long ownerId
) {}

