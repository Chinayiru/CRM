package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.ContractSaveRequest;
import com.example.cordyscrm.entity.Contract;
import com.example.cordyscrm.service.ContractService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Resource private ContractService service;
    @GetMapping public ApiResponse<List<Contract>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<Contract> get(@PathVariable Long id) { return ApiResponse.success(service.getContract(id)); }
    @PostMapping
    public ApiResponse<Contract> create(@Valid @RequestBody ContractSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<Contract> update(@PathVariable Long id, @Valid @RequestBody ContractSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

