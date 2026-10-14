package com.example.cordyscrm.controller;

import com.example.cordyscrm.common.ApiResponse;
import com.example.cordyscrm.dto.BootstrapAdminRequest;
import com.example.cordyscrm.dto.LoginRequest;
import com.example.cordyscrm.dto.LoginResponse;
import com.example.cordyscrm.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(security = {})
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.username(), request.password()));
    }

    @PostMapping("/bootstrap")
    @Operation(security = {})
    public ApiResponse<LoginResponse> bootstrap(@Valid @RequestBody BootstrapAdminRequest request) {
        return ApiResponse.success(authService.bootstrap(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization.substring(7).trim());
        return ApiResponse.success(null);
    }
}
