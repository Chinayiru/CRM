package com.example.cordyscrm.controller;

import com.example.cordyscrm.dto.ClueSaveRequest;
import com.example.cordyscrm.entity.Clue;
import com.example.cordyscrm.service.ClueService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clues")
public class ClueController {
    @Resource private ClueService service;
    @GetMapping public List<Clue> list() { return service.list(); }
    @GetMapping("/{id}") public Clue get(@PathVariable Long id) { return service.getClue(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Clue create(@Valid @RequestBody ClueSaveRequest request) { return service.create(request); }
    @PutMapping("/{id}")
    public Clue update(@PathVariable Long id, @Valid @RequestBody ClueSaveRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}

