package com.example.cordyscrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import com.example.cordyscrm.dto.UserRegistrationRequest;
import com.example.cordyscrm.entity.SysUser;
import com.example.cordyscrm.mapper.SysUserMapper;
import com.example.cordyscrm.service.SysUserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    private static final int ITERATIONS = 210_000;
    private static final int KEY_BITS = 256;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final Pattern PASSWORD_HASH_PATTERN = Pattern.compile("^pbkdf2-sha256\\$[0-9]+\\$[^$]+\\$[^$]+$");

    @Override
    public List<SysUser> list() {
        return lambdaQuery().orderByDesc(SysUser::getId).list();
    }

    @Override
    public SysUser getUser(Long id) { return getActive(id); }

    @Override
    public SysUser authenticate(String username, String password) {
        SysUser user = lambdaQuery().eq(SysUser::getUsername, username.trim()).one();
        if (user == null || !matchesPassword(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        if ("PENDING".equals(user.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "注册申请待管理员审核");
        }
        if ("REJECTED".equals(user.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "注册申请未通过审核");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号已停用");
        }
        return user;
    }

    @Override
    @Transactional
    public SysUser register(UserRegistrationRequest request) {
        validateUsername(request.username(), null);
        SysUser user = new SysUser();
        user.setUsername(request.username().trim());
        user.setNickname(trimToNull(request.nickname()));
        user.setPhone(trimToNull(request.phone()));
        user.setEmail(trimToNull(request.email()));
        user.setPasswordHash(hashPassword(request.password()));
        user.setStatus("DISABLED");
        user.setApprovalStatus("PENDING");
        user.setDeleted(0);
        save(user);
        return user;
    }

    @Override
    public List<SysUser> listPendingRegistrations() {
        return lambdaQuery().eq(SysUser::getApprovalStatus, "PENDING")
                .orderByAsc(SysUser::getCreatedAt).list();
    }

    @Override
    @Transactional
    public SysUser approveRegistration(Long userId, Long reviewerId) {
        SysUser user = getActive(userId);
        requirePendingApproval(user);
        boolean updated = lambdaUpdate().eq(SysUser::getId, userId)
                .eq(SysUser::getApprovalStatus, "PENDING")
                .set(SysUser::getApprovalStatus, "APPROVED")
                .set(SysUser::getStatus, "ENABLED")
                .set(SysUser::getUpdatedBy, reviewerId)
                .update();
        if (!updated) throw new ResponseStatusException(HttpStatus.CONFLICT, "注册申请已被其他管理员处理");
        return getActive(userId);
    }

    @Override
    @Transactional
    public SysUser rejectRegistration(Long userId, Long reviewerId) {
        SysUser user = getActive(userId);
        requirePendingApproval(user);
        boolean updated = lambdaUpdate().eq(SysUser::getId, userId)
                .eq(SysUser::getApprovalStatus, "PENDING")
                .set(SysUser::getApprovalStatus, "REJECTED")
                .set(SysUser::getStatus, "DISABLED")
                .set(SysUser::getUpdatedBy, reviewerId)
                .update();
        if (!updated) throw new ResponseStatusException(HttpStatus.CONFLICT, "注册申请已被其他管理员处理");
        return getActive(userId);
    }

    @Override
    @Transactional
    public SysUser create(SysUserSaveRequest request) {
        if (request.password() == null || request.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新用户密码至少需要8个字符");
        }
        validateUsername(request.username(), null);
        SysUser user = new SysUser();
        apply(user, request);
        user.setPasswordHash(hashPassword(request.password()));
        user.setStatus(request.status() == null ? "ENABLED" : request.status());
        user.setApprovalStatus("APPROVED");
        user.setDeleted(0);
        save(user);
        return user;
    }

    @Override
    @Transactional
    public SysUser update(Long id, SysUserSaveRequest request) {
        SysUser user = getActive(id);
        validateUsername(request.username(), id);
        apply(user, request);
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密码至少需要8个字符");
            }
            user.setPasswordHash(hashPassword(request.password()));
        }
        if (request.status() != null) user.setStatus(request.status());
        updateById(user);
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getActive(id);
        removeById(id);
    }

    private SysUser getActive(Long id) {
        SysUser user = getById(id);
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        return user;
    }

    private void requirePendingApproval(SysUser user) {
        if (!"PENDING".equals(user.getApprovalStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "该用户没有待处理的注册申请");
        }
    }

    private void validateUsername(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username.trim());
        if (excludeId != null) query.ne(SysUser::getId, excludeId);
        if (exists(query)) throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
    }

    private void apply(SysUser user, SysUserSaveRequest request) {
        user.setUsername(request.username().trim());
        user.setNickname(trimToNull(request.nickname()));
        user.setPhone(trimToNull(request.phone()));
        user.setEmail(trimToNull(request.email()));
        user.setDepartmentId(request.departmentId());
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String result = value.trim();
        return result.isEmpty() ? null : result;
    }

    private String hashPassword(String password) {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_BITS);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().withoutPadding().encodeToString(salt)
                    + "$" + Base64.getEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("密码哈希失败", ex);
        } finally {
            spec.clearPassword();
        }
    }

    private boolean matchesPassword(String password, String encoded) {
        if (password == null || encoded == null || !PASSWORD_HASH_PATTERN.matcher(encoded).matches()) return false;
        String[] parts = encoded.split("\\$", -1);
        try {
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 2_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expected.length * 8);
            try {
                byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
                return MessageDigest.isEqual(expected, actual);
            } finally {
                spec.clearPassword();
            }
        } catch (Exception exception) {
            return false;
        }
    }
}
