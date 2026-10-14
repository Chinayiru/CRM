package com.example.cordyscrm.dto;

public record LoginResponse(String token, String tokenType, long expiresIn, SysUserResponse user) {
}
