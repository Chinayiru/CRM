package com.example.cordyscrm.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author cxt
 * @Date 2026-10-13 11:37
 */

@RestController
public class HealthController {
    private final JdbcTemplate jdbcTemplate;


    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        return Map.of(
                "status", "UP",
                "database", result != null && result == 1 ? "UP" : "DOWN"
        );

    }
}
