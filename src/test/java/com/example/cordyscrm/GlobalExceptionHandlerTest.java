package com.example.cordyscrm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * 全局异常处理的行为测试。
 *
 * <p>重点回归：外键约束失败曾经直接返回 500 并且不给出任何原因，
 * 现在必须映射为带中文提示的 4xx 响应。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("负责人不存在：外键约束失败映射为 404，而不是 500")
    void foreignKeyViolationMapsToNotFound() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"name\":\"外键测试客户\",\"ownerId\":999999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("资源不存在：引用的关联数据可能已被删除"))
                .andExpect(jsonPath("$.message").value(not(containsString("foreign key"))))
                .andExpect(jsonPath("$.message").value(not(containsString("SQL"))))
                .andExpect(jsonPath("$.path").value("/api/customers"));
    }

    @Test
    @DisplayName("同名部门：返回 409 且携带可读原因")
    void duplicateDepartmentNameReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"deptName\":\"技术部\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("同一上级部门下已存在同名部门"));
    }

    @Test
    @DisplayName("唯一索引冲突：映射为 409 而不是 500")
    void duplicateUsernameReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\":\"admin\",\"password\":\"passw0rd123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    @DisplayName("请求体格式错误：返回 400 与明确提示")
    void malformedBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("请求体缺失或 JSON 格式不正确"));
    }

    @Test
    @DisplayName("不存在的接口路径：返回 404 统一结构")
    void unknownPathReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/not-exists"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.path").value("/api/not-exists"));
    }
}
