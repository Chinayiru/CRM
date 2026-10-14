package com.example.cordyscrm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 新增或修改商机的请求参数。 */
public record OpportunitySaveRequest(
        @NotBlank(message = "商机名称不能为空")
        @Size(max = 200, message = "商机名称不能超过200个字符")
        String name,

        @NotNull(message = "客户ID不能为空")
        Long customerId,

        @DecimalMin(value = "0.00", message = "预计金额不能小于0")
        @Digits(integer = 16, fraction = 2, message = "预计金额最多16位整数、2位小数")
        BigDecimal amount,

        @Pattern(regexp = "INITIAL_CONTACT|NEEDS_CONFIRMED|PROPOSAL|NEGOTIATION|WON|LOST",
                message = "商机阶段只能是 INITIAL_CONTACT、NEEDS_CONFIRMED、PROPOSAL、NEGOTIATION、WON 或 LOST")
        String stage,

        LocalDate expectTime,

        @Min(value = 0, message = "赢率不能小于0")
        @Max(value = 100, message = "赢率不能大于100")
        Integer winRate,

        @Size(max = 1000, message = "输单原因不能超过1000个字符")
        String lostReason,

        LocalDateTime closedAt,

        @NotNull(message = "负责人ID不能为空")
        Long ownerId
) {
}
