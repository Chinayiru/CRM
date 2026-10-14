package com.example.cordyscrm.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.cordyscrm.dto.ClueSaveRequest;
import com.example.cordyscrm.entity.Clue;

public interface ClueService extends IService<Clue> {
    Clue getClue(Long id);
    Clue create(ClueSaveRequest request);
    Clue update(Long id, ClueSaveRequest request);
    void delete(Long id);
}

