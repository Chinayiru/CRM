package com.example.cordyscrm.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cordys CRM API",
                version = "v1",
                description = "CRM 后端接口。除登录、首次管理员初始化和健康检查外，所有接口需要 Bearer token。"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "opaque token",
        description = "调用 POST /api/auth/login 或首次初始化接口获取")
public class OpenApiConfig {
}
