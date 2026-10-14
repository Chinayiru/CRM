package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.CustomerSaveRequest;
import com.example.cordyscrm.entity.Customer;
import com.example.cordyscrm.service.CustomerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @GetMapping
    public ApiResponse<List<Customer>> list() {
        return ApiResponse.success(customerService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> get(@PathVariable Long id) {
        return ApiResponse.success(customerService.getCustomer(id));
    }

    @PostMapping
    public ApiResponse<Customer> create(@Valid @RequestBody CustomerSaveRequest request) {
        return ApiResponse.success(customerService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerSaveRequest request) {
        return ApiResponse.success(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success(null);
    }
}
