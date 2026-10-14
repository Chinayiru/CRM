package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysDictSaveRequest;
import com.example.cordyscrm.entity.SysDict;
import com.example.cordyscrm.service.SysDictService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dicts")
public class SysDictController {
    @Resource private SysDictService service;
    @GetMapping public List<SysDict> list() { return service.list(); }
    @GetMapping("/{id}") public SysDict get(@PathVariable Long id) { return service.getSysDict(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SysDict create(@Valid @RequestBody SysDictSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public SysDict update(@PathVariable Long id, @Valid @RequestBody SysDictSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

