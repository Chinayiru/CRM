package com.example.cordyscrm;

import com.example.cordyscrm.dto.ClueSaveRequest;
import com.example.cordyscrm.dto.ContractSaveRequest;
import com.example.cordyscrm.dto.CustomerSaveRequest;
import com.example.cordyscrm.dto.DeptSaveRequest;
import com.example.cordyscrm.dto.FollowRecordSaveRequest;
import com.example.cordyscrm.dto.OpportunitySaveRequest;
import com.example.cordyscrm.dto.SysDictSaveRequest;
import com.example.cordyscrm.dto.SysMenuSaveRequest;
import com.example.cordyscrm.dto.SysOperationLogSaveRequest;
import com.example.cordyscrm.dto.SysRoleMenuSaveRequest;
import com.example.cordyscrm.dto.SysRoleSaveRequest;
import com.example.cordyscrm.dto.SysUserRoleSaveRequest;
import com.example.cordyscrm.dto.SysUserSaveRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试环境自身与校验约束的一致性检查。
 *
 * <p>不依赖 Spring 上下文的断言使用 Bean Validation 直接执行，避免为每条约束都起一次容器。
 */
@SpringBootTest
@ActiveProfiles("test")
class TestEnvironmentTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("测试库已建表，并写入测试种子数据")
    void testDatabaseIsSeeded() {
        Integer tables = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM information_schema.tables
                WHERE table_schema = DATABASE() AND table_name IN ('sys_user','sys_dept','customer','opportunity','contract')
                """, Integer.class);
        assertThat(tables).isEqualTo(5);

        assertThat(count("SELECT COUNT(*) FROM sys_user WHERE id = 1")).isPositive();
        assertThat(count("SELECT COUNT(*) FROM sys_dept WHERE dept_name = '技术部'")).isPositive();
        assertThat(count("SELECT COUNT(*) FROM sys_role WHERE role_key = 'admin'")).isPositive();
    }

    @Test
    @DisplayName("客户校验约束与建表列定义一致，并给出中文提示")
    void customerConstraintsMatchSchema() {
        CustomerSaveRequest invalid = new CustomerSaveRequest(
                " ".repeat(201), null, null, null, null, null, null, null, "UNKNOWN", null);
        Set<ConstraintViolation<CustomerSaveRequest>> violations = validator.validate(invalid);

        assertThat(violatedFields(violations)).contains("name", "ownerId", "status");
        assertThat(violations).allSatisfy(violation ->
                assertThat(violation.getMessage()).isNotBlank());
        assertThat(messagesOf(violations, "name")).contains("客户名称不能为空", "客户名称不能超过200个字符");
        assertThat(messagesOf(violations, "ownerId")).contains("负责人ID不能为空");
    }

    @Test
    @DisplayName("各业务对象的必填校验都有可读提示，不出现默认提示语")
    void allRequestDtosHaveReadableMessages() {
        List<Set<? extends ConstraintViolation<?>>> violationGroups = List.of(
                validate(new DeptSaveRequest("", null, -1, "UNKNOWN")),
                validate(new SysUserSaveRequest("", null, null, null, "not-an-email", null, "UNKNOWN")),
                validate(new ClueSaveRequest("", null, null, null, "UNKNOWN", null)),
                validate(new OpportunitySaveRequest("", null, null, "UNKNOWN", null, 200, null, null, null)),
                validate(new ContractSaveRequest("", "", null, null, null, null, null, "UNKNOWN", null)),
                validate(new FollowRecordSaveRequest("", null, null, "", null, null)),
                validate(new SysRoleSaveRequest("", "", "", null, "UNKNOWN")),
                validate(new SysMenuSaveRequest(null, "", "UNKNOWN", null, null, -1, "UNKNOWN")),
                validate(new SysDictSaveRequest("", "", "", -1, "UNKNOWN")),
                validate(new SysOperationLogSaveRequest(null, "", "", null, null, "UNKNOWN", null, null, -1L)),
                validate(new SysUserRoleSaveRequest(null, null)),
                validate(new SysRoleMenuSaveRequest(null, null)));

        violationGroups.forEach(violations -> {
            assertThat(violations).as("每个 DTO 都应触发约束违规").isNotEmpty();
            assertThat(violations).allSatisfy(violation -> {
                assertThat(violation.getMessage()).as("校验提示不能为空").isNotBlank();
                assertThat(violation.getMessage())
                        .as("必须使用自定义中文提示，而不是 Hibernate 默认提示")
                        .doesNotContain("must not be null")
                        .doesNotContain("must not be blank")
                        .doesNotContain("size must be between");
            });
        });
    }

    private Long count(String sql) {
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    private <T> Set<ConstraintViolation<T>> validate(T target) {
        return validator.validate(target);
    }

    private List<String> violatedFields(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(this::fieldName).toList();
    }

    private List<String> messagesOf(Set<? extends ConstraintViolation<?>> violations, String field) {
        return violations.stream()
                .filter(violation -> field.equals(fieldName(violation)))
                .map(ConstraintViolation::getMessage)
                .toList();
    }

    private String fieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int separator = path.lastIndexOf('.');
        return separator < 0 ? path : path.substring(separator + 1);
    }
}
