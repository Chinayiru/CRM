package com.example.cordyscrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClueSaveRequest(
        @NotBlank(message="线索名称不能为空") @Size(max=150) String name,
        @Size(max=32) String phone,
        @Size(max=100) String source,
        @NotNull(message="负责人ID不能为空") Long ownerId,
        @Pattern(regexp="NEW|FOLLOWING|CONVERTED|DISCARDED", message="线索状态无效") String status,
        String remark
) {}

