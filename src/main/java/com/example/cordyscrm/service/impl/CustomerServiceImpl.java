package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.CustomerSaveRequest;
import com.example.cordyscrm.entity.Customer;
import com.example.cordyscrm.mapper.CustomerMapper;
import com.example.cordyscrm.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public List<Customer> list() {
        return lambdaQuery()
                .orderByDesc(Customer::getCreatedAt)
                .orderByDesc(Customer::getId)
                .list();
    }

    @Override
    public Customer getCustomer(Long id) {
        return getActiveCustomer(id);
    }

    @Override
    @Transactional
    public Customer create(CustomerSaveRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        customer.setStatus(request.status() == null ? "ACTIVE" : request.status());
        customer.setDeleted(0);
        save(customer);
        return customer;
    }

    @Override
    @Transactional
    public Customer update(Long id, CustomerSaveRequest request) {
        Customer customer = getActiveCustomer(id);
        applyRequest(customer, request);
        if (request.status() != null) {
            customer.setStatus(request.status());
        }
        updateById(customer);
        return customer;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getActiveCustomer(id);
        // Customer 上的 @TableLogic 会执行逻辑删除。
        removeById(id);
    }

    private Customer getActiveCustomer(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在");
        }
        return customer;
    }

    private void applyRequest(Customer customer, CustomerSaveRequest request) {
        customer.setName(request.name().trim());
        customer.setCustomerType(trimToNull(request.customerType()));
        customer.setContact(trimToNull(request.contact()));
        customer.setPhone(trimToNull(request.phone()));
        customer.setIndustry(trimToNull(request.industry()));
        customer.setOwnerId(request.ownerId());
        customer.setSource(trimToNull(request.source()));
        customer.setAddress(trimToNull(request.address()));
        customer.setSourceClueId(request.sourceClueId());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
