package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.DeptSaveRequest;
import com.example.cordyscrm.entity.SysDept;
import com.example.cordyscrm.mapper.SysDeptMapper;
import com.example.cordyscrm.service.SysDeptService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author cxt
 * @Date 2026-10-14 00:45
 */

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
    @Override
    public List<SysDept> list() {
        return lambdaQuery()
                .orderByAsc(SysDept::getSortNo)
                .orderByAsc(SysDept::getId)
                .list();
    }

    @Override
    @Transactional
    public SysDept create(DeptSaveRequest request) {
        validateParent(request.parentId(), null);
        validateUniqueName(request.parentId(), request.deptName(), null);

        SysDept dept = new SysDept();
        dept.setParentId(request.parentId());
        dept.setDeptName(request.deptName().trim());
        dept.setSortNo(request.sortNo() == null ? 0 : request.sortNo());
        dept.setStatus(request.status() == null ? "ENABLED" : request.status());
        dept.setDeleted(0);

        save(dept);
        return dept;
    }

    @Override
    @Transactional
    public SysDept update(Long id, DeptSaveRequest request) {
        SysDept dept = getActiveDept(id);

        validateParent(request.parentId(), id);
        validateUniqueName(request.parentId(), request.deptName(), id);

        dept.setParentId(request.parentId());
        dept.setDeptName(request.deptName().trim());
        dept.setSortNo(request.sortNo() == null ? 0 : request.sortNo());

        if (request.status() != null) {
            dept.setStatus(request.status());
        }

        updateById(dept);
        return dept;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getActiveDept(id);

        boolean hasChildren = lambdaQuery()
                .eq(SysDept::getParentId, id)
                .exists();

        if (hasChildren) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "该部门还有下级部门，不能删除"
            );
        }

        // SysDept 中的 @TableLogic 会将其标记为逻辑删除
        removeById(id);
    }

    private SysDept getActiveDept(Long id) {
        SysDept dept = getById(id);
        if (dept == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "部门不存在");
        }
        return dept;
    }

    private void validateParent(Long parentId, Long currentDeptId) {
        if (parentId == null) {
            return;
        }

        Set<Long> visited = new HashSet<>();
        Long cursor = parentId;

        while (cursor != null) {
            if (currentDeptId != null && cursor.equals(currentDeptId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "不能将部门设置为自己的下级部门"
                );
            }

            if (!visited.add(cursor)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "部门层级存在循环关系，请先检查数据"
                );
            }

            SysDept parent = getActiveDept(cursor);
            cursor = parent.getParentId();
        }
    }

    private void validateUniqueName(Long parentId, String deptName, Long excludeId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptName, deptName.trim());

        if (parentId == null) {
            wrapper.isNull(SysDept::getParentId);
        } else {
            wrapper.eq(SysDept::getParentId, parentId);
        }

        if (excludeId != null) {
            wrapper.ne(SysDept::getId, excludeId);
        }

        if (exists(wrapper)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "同一上级部门下已存在同名部门"
            );
        }
    }
}
