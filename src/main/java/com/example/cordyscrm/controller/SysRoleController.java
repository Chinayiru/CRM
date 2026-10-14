package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysRoleSaveRequest;
import com.example.cordyscrm.entity.SysRole;
import com.example.cordyscrm.service.SysRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class SysRoleController {
    @Resource private SysRoleService service;
    @GetMapping public List<SysRole> list() { return service.list(); }
    @GetMapping("/{id}") public SysRole get(@PathVariable Long id) { return service.getSysRole(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SysRole create(@Valid @RequestBody SysRoleSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public SysRole update(@PathVariable Long id, @Valid @RequestBody SysRoleSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

