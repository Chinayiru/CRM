package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.ContractSaveRequest;
import com.example.cordyscrm.entity.Contract;

public interface ContractService extends IService<Contract> {
    Contract getContract(Long id);
    Contract create(ContractSaveRequest request);
    Contract update(Long id, ContractSaveRequest request);
    void delete(Long id);
}

