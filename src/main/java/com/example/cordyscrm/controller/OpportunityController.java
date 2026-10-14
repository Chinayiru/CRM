package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.OpportunitySaveRequest;
import com.example.cordyscrm.entity.Opportunity;
import com.example.cordyscrm.service.OpportunityService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {
    @Resource private OpportunityService service;
    @GetMapping public List<Opportunity> list() { return service.list(); }
    @GetMapping("/{id}") public Opportunity get(@PathVariable Long id) { return service.getOpportunity(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Opportunity create(@Valid @RequestBody OpportunitySaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public Opportunity update(@PathVariable Long id, @Valid @RequestBody OpportunitySaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

