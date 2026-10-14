package com.example.cordyscrm.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAccessInterceptor implements HandlerInterceptor {
    private final AdminAccessService adminAccessService;

    public AdminAccessInterceptor(AdminAccessService adminAccessService) {
        this.adminAccessService = adminAccessService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.AUTHENTICATED_USER_ID);
        adminAccessService.requireAdministrator(userId);
        return true;
    }
}
