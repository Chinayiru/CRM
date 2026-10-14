package com.example.cordyscrm.service.impl;

import com.example.cordyscrm.dto.SysRoleMenuSaveRequest;
import com.example.cordyscrm.entity.SysRoleMenu;
import com.example.cordyscrm.mapper.SysRoleMenuMapper;
import com.example.cordyscrm.service.SysRoleMenuService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {
    @Resource private SysRoleMenuMapper mapper;
    @Override public List<SysRoleMenu> list(){return mapper.findAll();}
    @Override public SysRoleMenu get(Long roleId,Long menuId){
        SysRoleMenu value=mapper.find(roleId,menuId);
        if(value==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"角色菜单关联不存在");
        return value;
    }
    @Override @Transactional public SysRoleMenu create(SysRoleMenuSaveRequest request){
        if(mapper.find(request.roleId(),request.menuId())!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"该角色已拥有此菜单权限");
        mapper.insert(request.roleId(),request.menuId());
        return mapper.find(request.roleId(),request.menuId());
    }
    @Override @Transactional public void delete(Long roleId,Long menuId){
        if(mapper.delete(roleId,menuId)==0) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"角色菜单关联不存在");
    }
}
