package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.ContractSaveRequest;
import com.example.cordyscrm.entity.Contract;
import com.example.cordyscrm.service.ContractService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Resource private ContractService service;
    @GetMapping public List<Contract> list() { return service.list(); }
    @GetMapping("/{id}") public Contract get(@PathVariable Long id) { return service.getContract(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Contract create(@Valid @RequestBody ContractSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public Contract update(@PathVariable Long id, @Valid @RequestBody ContractSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

