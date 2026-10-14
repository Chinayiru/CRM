package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.SysMenuSaveRequest;
import com.example.cordyscrm.entity.SysMenu;

public interface SysMenuService extends IService<SysMenu> {
    SysMenu getSysMenu(Long id);
    SysMenu create(SysMenuSaveRequest request);
    SysMenu update(Long id, SysMenuSaveRequest request);
    void delete(Long id);
}

