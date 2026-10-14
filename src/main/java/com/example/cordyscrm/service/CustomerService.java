package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.CustomerSaveRequest;
import com.example.cordyscrm.entity.Customer;

public interface CustomerService extends IService<Customer> {

    Customer getCustomer(Long id);

    Customer create(CustomerSaveRequest request);

    Customer update(Long id, CustomerSaveRequest request);

    void delete(Long id);
}
