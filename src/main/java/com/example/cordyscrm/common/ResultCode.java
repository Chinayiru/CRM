package com.example.cordyscrm.common;

import org.springframework.http.HttpStatus;

/**
 * 统一业务状态码。
 *
 * <p>响应体中的 {@code code} 只使用这里定义的取值，前端按 {@code code != 200} 判定失败，
 * HTTP 状态码仅用于表达传输层语义，两者一一对应。
 */
public enum ResultCode {

    /** 请求成功。 */
    SUCCESS(HttpStatus.OK, "success"),

    /** 参数校验失败或业务规则不满足。 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "请求参数不合法"),

    /** 认证失败（Sa-Token 接入后使用）。 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "未认证或登录状态已失效"),

    /** 权限不足（Sa-Token 接入后使用）。 */
    FORBIDDEN(HttpStatus.FORBIDDEN, "没有访问该资源的权限"),

    /** 资源不存在。 */
    NOT_FOUND(HttpStatus.NOT_FOUND, "资源不存在"),

    /** 与已有数据冲突，例如同名部门、重复的角色授权。 */
    CONFLICT(HttpStatus.CONFLICT, "数据冲突"),

    /** 服务端异常，响应体不暴露内部细节。 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");

    private final HttpStatus httpStatus;
    private final String message;

    ResultCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int code() {
        return httpStatus.value();
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String message() {
        return message;
    }
}
