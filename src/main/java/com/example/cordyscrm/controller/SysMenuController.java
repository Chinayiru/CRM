package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysMenuSaveRequest;
import com.example.cordyscrm.entity.SysMenu;
import com.example.cordyscrm.service.SysMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class SysMenuController {
    @Resource private SysMenuService service;
    @GetMapping public List<SysMenu> list() { return service.list(); }
    @GetMapping("/{id}") public SysMenu get(@PathVariable Long id) { return service.getSysMenu(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SysMenu create(@Valid @RequestBody SysMenuSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public SysMenu update(@PathVariable Long id, @Valid @RequestBody SysMenuSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

