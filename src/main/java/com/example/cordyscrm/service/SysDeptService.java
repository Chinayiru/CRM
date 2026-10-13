package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.DeptSaveRequest;
import com.example.cordyscrm.entity.SysDept;
/**
 * @Author cxt
 * @Date 2026-10-14 00:42
 */
public interface SysDeptService extends IService<SysDept> {


    SysDept create(DeptSaveRequest request);

    SysDept update(Long id, DeptSaveRequest request);

    void delete(Long id);

}
