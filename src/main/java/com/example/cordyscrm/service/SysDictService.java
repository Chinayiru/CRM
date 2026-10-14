package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.SysDictSaveRequest;
import com.example.cordyscrm.entity.SysDict;

public interface SysDictService extends IService<SysDict> {
    SysDict getSysDict(Long id);
    SysDict create(SysDictSaveRequest request);
    SysDict update(Long id, SysDictSaveRequest request);
    void delete(Long id);
}

