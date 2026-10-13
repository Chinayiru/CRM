package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.DeptSaveRequest;
import com.example.cordyscrm.entity.SysDept;
import com.example.cordyscrm.service.SysDeptService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author cxt
 * @Date 2026-10-14 00:48
 */

@RestController
@RequestMapping("/api/departments")
public class SysDeptController {

    @Resource
    private SysDeptService deptService;

    @GetMapping
    public List<SysDept> list() {
        return deptService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SysDept create(@Valid @RequestBody DeptSaveRequest request) {
        return deptService.create(request);
    }

    @PutMapping("/{id}")
    public SysDept update(
            @PathVariable Long id,
            @Valid @RequestBody DeptSaveRequest request
    ) {
        return deptService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deptService.delete(id);
    }
}
