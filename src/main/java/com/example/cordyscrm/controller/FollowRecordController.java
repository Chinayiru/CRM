package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.FollowRecordSaveRequest;
import com.example.cordyscrm.entity.FollowRecord;
import com.example.cordyscrm.service.FollowRecordService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/follow-records")
public class FollowRecordController {
    @Resource private FollowRecordService service;
    @GetMapping public List<FollowRecord> list() { return service.list(); }
    @GetMapping("/{id}") public FollowRecord get(@PathVariable Long id) { return service.getFollowRecord(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public FollowRecord create(@Valid @RequestBody FollowRecordSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public FollowRecord update(@PathVariable Long id, @Valid @RequestBody FollowRecordSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

