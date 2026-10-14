package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysUserResponse;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import com.example.cordyscrm.service.SysUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class SysUserController {
    @Resource private SysUserService userService;

    @GetMapping
    public List<SysUserResponse> list() {
        return userService.list().stream().map(SysUserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SysUserResponse get(@PathVariable Long id) {
        return SysUserResponse.from(userService.getUser(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SysUserResponse create(@Valid @RequestBody SysUserSaveRequest request) {
        return SysUserResponse.from(userService.create(request));
    }

    @PutMapping("/{id}")
    public SysUserResponse update(@PathVariable Long id, @Valid @RequestBody SysUserSaveRequest request) {
        return SysUserResponse.from(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { userService.delete(id); }
}
