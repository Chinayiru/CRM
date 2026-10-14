package com.example.cordyscrm.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final AdminAccessInterceptor adminAccessInterceptor;

    public WebSecurityConfig(AuthInterceptor authInterceptor, AdminAccessInterceptor adminAccessInterceptor) {
        this.authInterceptor = authInterceptor;
        this.adminAccessInterceptor = adminAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/api/auth/bootstrap", "/api/health");
        registry.addInterceptor(adminAccessInterceptor)
                .addPathPatterns(
                        "/api/admin/**",
                        "/api/users", "/api/users/**",
                        "/api/user-roles", "/api/user-roles/**",
                        "/api/roles", "/api/roles/**",
                        "/api/role-menus", "/api/role-menus/**",
                        "/api/menus", "/api/menus/**",
                        "/api/departments", "/api/departments/**",
                        "/api/dicts", "/api/dicts/**",
                        "/api/operation-logs", "/api/operation-logs/**");
    }
}
