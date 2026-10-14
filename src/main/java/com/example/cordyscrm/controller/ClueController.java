package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.ClueSaveRequest;
import com.example.cordyscrm.entity.Clue;
import com.example.cordyscrm.service.ClueService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clues")
public class ClueController {
    @Resource private ClueService service;
    @GetMapping public ApiResponse<List<Clue>> list() { return ApiResponse.success(service.list()); }
    @GetMapping("/{id}") public ApiResponse<Clue> get(@PathVariable Long id) { return ApiResponse.success(service.getClue(id)); }
    @PostMapping
    public ApiResponse<Clue> create(@Valid @RequestBody ClueSaveRequest request) { return ApiResponse.success(service.create(request)); }
    @PutMapping("/{id}")
    public ApiResponse<Clue> update(@PathVariable Long id, @Valid @RequestBody ClueSaveRequest request) { return ApiResponse.success(service.update(id, request)); }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) { service.delete(id);
        return ApiResponse.success(null);
    }
}

