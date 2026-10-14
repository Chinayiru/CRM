package com.example.cordyscrm.security;

import com.example.cordyscrm.dto.BootstrapAdminRequest;
import com.example.cordyscrm.dto.LoginResponse;
import com.example.cordyscrm.dto.SysUserResponse;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import com.example.cordyscrm.dto.UserRegistrationRequest;
import com.example.cordyscrm.entity.SysUser;
import com.example.cordyscrm.entity.SysRole;
import com.example.cordyscrm.mapper.SysRoleMapper;
import com.example.cordyscrm.mapper.SysUserRoleMapper;
import com.example.cordyscrm.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ConcurrentHashMap<String, LoginSession> sessions = new ConcurrentHashMap<>();
    private final SysUserService userService;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final long tokenTtlSeconds;
    private final String bootstrapSecret;

    public AuthService(
            SysUserService userService,
            SysRoleMapper roleMapper,
            SysUserRoleMapper userRoleMapper,
            @Value("${app.auth.token-ttl-seconds:28800}") long tokenTtlSeconds,
            @Value("${app.auth.bootstrap-secret:}") String bootstrapSecret) {
        if (tokenTtlSeconds < 60) throw new IllegalArgumentException("auth token TTL must be at least 60 seconds");
        this.userService = userService;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.tokenTtlSeconds = tokenTtlSeconds;
        this.bootstrapSecret = bootstrapSecret;
    }

    public LoginResponse login(String username, String password) {
        return createSession(userService.authenticate(username, password));
    }

    public SysUserResponse register(UserRegistrationRequest request) {
        return SysUserResponse.from(userService.register(request));
    }

    /** Creates the first account only when an explicit one-time setup secret is configured. */
    @Transactional
    public synchronized LoginResponse bootstrap(BootstrapAdminRequest request) {
        if (bootstrapSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "尚未配置首次管理员初始化密钥");
        }
        if (!MessageDigest.isEqual(bootstrapSecret.getBytes(StandardCharsets.UTF_8),
                request.bootstrapSecret().getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "初始化密钥不正确");
        }
        if (userService.count() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "系统已初始化，请使用登录接口");
        }
        SysUser user = userService.create(new SysUserSaveRequest(
                request.username(), request.password(), request.nickname(), null, null, null, "ENABLED"));
        SysRole adminRole = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, "admin"));
        if (adminRole == null) {
            adminRole = new SysRole();
            adminRole.setRoleName("系统管理员");
            adminRole.setRoleKey("admin");
            adminRole.setDataScope("ALL");
            adminRole.setStatus("ENABLED");
            adminRole.setDeleted(0);
            roleMapper.insert(adminRole);
        } else if (!"ENABLED".equals(adminRole.getStatus())) {
            adminRole.setStatus("ENABLED");
            roleMapper.updateById(adminRole);
        }
        userRoleMapper.insert(user.getId(), adminRole.getId());
        return createSession(user);
    }

    public Long resolveUserId(String token) {
        if (token == null || token.isBlank()) return null;
        LoginSession session = sessions.get(token);
        if (session == null) return null;
        if (session.expiresAtEpochSecond() <= Instant.now().getEpochSecond()) {
            sessions.remove(token, session);
            return null;
        }
        try {
            SysUser user = userService.getUser(session.userId());
            if (!"ENABLED".equals(user.getStatus()) || !"APPROVED".equals(user.getApprovalStatus())) {
                sessions.remove(token, session);
                return null;
            }
        } catch (ResponseStatusException exception) {
            sessions.remove(token, session);
            return null;
        }
        return session.userId();
    }

    public void logout(String token) {
        if (token != null) sessions.remove(token);
    }

    public long tokenTtlSeconds() {
        return tokenTtlSeconds;
    }

    private LoginResponse createSession(SysUser user) {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        sessions.put(token, new LoginSession(user.getId(), Instant.now().getEpochSecond() + tokenTtlSeconds));
        return new LoginResponse(token, "Bearer", tokenTtlSeconds, SysUserResponse.from(user));
    }

    private record LoginSession(Long userId, long expiresAtEpochSecond) {
    }
}
