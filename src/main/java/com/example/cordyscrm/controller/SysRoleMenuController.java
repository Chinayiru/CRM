package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.SysRoleMenuSaveRequest;
import com.example.cordyscrm.entity.SysRoleMenu;
import com.example.cordyscrm.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role-menus")
public class SysRoleMenuController {
    @Resource private SysRoleMenuService service;
    @GetMapping public List<SysRoleMenu> list(){return service.list();}
    @GetMapping("/{roleId}/{menuId}") public SysRoleMenu get(@PathVariable Long roleId,@PathVariable Long menuId){return service.get(roleId,menuId);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public SysRoleMenu create(@Valid @RequestBody SysRoleMenuSaveRequest request){return service.create(request);}
    @DeleteMapping("/{roleId}/{menuId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long roleId,@PathVariable Long menuId){service.delete(roleId,menuId);}
}
