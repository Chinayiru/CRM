package com.example.cordyscrm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 统一响应体与状态码契约测试。
 *
 * <p>断言所有接口都返回 {@code code/message/data} 结构，并且 HTTP 状态码与业务码一致。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiResponseContractTest {

    private static final String CUSTOMERS = "/api/customers";
    private static final String CUSTOMER_JSON = """
            {"name":"契约测试客户","ownerId":1,"customerType":"enterprise","source":"website"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("新增客户：返回 200 业务码与完整响应字段")
    void createCustomerReturnsUnifiedBody() throws Exception {
        mockMvc.perform(post(CUSTOMERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(CUSTOMER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.name").value("契约测试客户"))
                .andExpect(jsonPath("$.data.ownerId").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.path").doesNotExist())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("查询不存在的客户：HTTP 404 且业务码同为 404")
    void missingCustomerReturnsNotFound() throws Exception {
        mockMvc.perform(get(CUSTOMERS + "/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("客户不存在"))
                .andExpect(jsonPath("$.path").value(CUSTOMERS + "/999999"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("参数校验失败：HTTP 400、业务码 400，并逐字段返回原因")
    void validationFailureReturnsFieldErrors() throws Exception {
        mockMvc.perform(post(CUSTOMERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"\",\"ownerId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("客户名称不能为空")))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("客户名称不能为空"));
    }

    @Test
    @DisplayName("删除客户：返回 200 业务码，data 为空")
    void deleteCustomerReturnsSuccessBody() throws Exception {
        jdbcTemplate.update("""
                INSERT INTO customer (name, owner_id, status, deleted) VALUES (?, ?, 'ACTIVE', 0)
                """, "待删除客户", 1L);
        Long id = jdbcTemplate.queryForObject("SELECT id FROM customer WHERE name = ?", Long.class, "待删除客户");

        mockMvc.perform(delete(CUSTOMERS + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("响应体不暴露密码哈希等内部字段")
    void userResponseHidesPasswordHash() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(content().string(not(containsString("passwordHash"))))
                .andExpect(content().string(not(containsString("password_hash"))));
    }
}
