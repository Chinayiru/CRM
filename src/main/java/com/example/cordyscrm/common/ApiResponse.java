package com.example.cordyscrm.common;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 所有接口的统一响应体。
 *
 * <p>成功时 {@code code} 固定为 200，业务数据放在 {@code data}；
 * 失败时 {@code code} 取 {@link ResultCode} 中的值，原因放在 {@code message}，
 * 字段级校验失败额外放在 {@code errors}，{@code data} 恒为 {@code null}。
 *
 * @param code      业务状态码，见 {@link ResultCode}
 * @param message   结果描述，失败时为可读中文原因
 * @param path      请求路径，便于前端与日志定位
 * @param data      业务数据，失败时为 null
 * @param errors    字段级校验错误，无则为空数组
 * @param timestamp 响应生成时间
 * @param <T>       业务数据类型
 */
public record ApiResponse<T>(
        int code,
        String message,
        String path,
        T data,
        List<GlobalExceptionHandler.ErrorField> errors,
        LocalDateTime timestamp) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResultCode.SUCCESS.code(), ResultCode.SUCCESS.message(),
                null, data, List.of(), LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(int code, String message, String path) {
        return failure(code, message, path, List.of());
    }

    public static <T> ApiResponse<T> failure(
            int code, String message, String path, List<GlobalExceptionHandler.ErrorField> errors) {
        return new ApiResponse<>(code, message, path, null, List.copyOf(errors), LocalDateTime.now());
    }
}
