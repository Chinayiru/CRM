package com.example.cordyscrm.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 全局异常处理：把所有异常收敛成统一响应体，并给出可读的失败原因。
 *
 * <p>处理原则：
 * <ul>
 *   <li>参数校验、业务冲突、资源不存在返回明确原因，便于前端直接展示。</li>
 *   <li>数据库完整性异常翻译为可读提示，避免把 SQL 细节暴露给调用方，也不再一律返回 500。</li>
 *   <li>服务端异常记录完整堆栈，响应体只返回兜底文案。</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 外键约束失败：被引用的关联数据不存在。 */
    private static final int SQL_FOREIGN_KEY_CHILD = 1452;

    /** 外键约束失败：数据已被其他记录引用，不能删除或修改。 */
    private static final int SQL_FOREIGN_KEY_PARENT = 1451;

    /** 唯一索引冲突。 */
    private static final int SQL_DUPLICATE_ENTRY = 1062;

    /** 参数校验失败，逐字段返回原因。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ErrorField> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorField(error.getField(), resolveMessage(error)))
                .toList();
        String message = fields.stream()
                .map(field -> field.field() + ": " + field.message())
                .reduce((first, next) -> first + "; " + next)
                .orElse(ResultCode.BAD_REQUEST.message());
        log.warn("参数校验失败 {} {} -> {}", request.getMethod(), request.getRequestURI(), message);
        return build(ResultCode.BAD_REQUEST, message, request, fields);
    }

    /** 方法参数（路径变量、查询参数）校验失败。 */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleHandlerMethodValidation(
            HandlerMethodValidationException exception, HttpServletRequest request) {
        return build(ResultCode.BAD_REQUEST, ResultCode.BAD_REQUEST.message(), request, List.of());
    }

    /** {@code @Validated} 在方法入口抛出的约束违规。 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        List<ErrorField> fields = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorField(lastNode(violation), violation.getMessage()))
                .toList();
        String message = fields.stream()
                .map(field -> field.field() + ": " + field.message())
                .reduce((first, next) -> first + "; " + next)
                .orElse(ResultCode.BAD_REQUEST.message());
        return build(ResultCode.BAD_REQUEST, message, request, fields);
    }

    /** 请求体缺失或 JSON 格式错误。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleUnreadableMessage(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return build(ResultCode.BAD_REQUEST, "请求体缺失或 JSON 格式不正确", request, List.of());
    }

    /**
     * 数据库完整性异常：唯一索引冲突、外键约束失败等。
     *
     * <p>这类异常本质上都是调用方输入不合法或引用了不存在的数据，属于 4xx，
     * 不应再落到兜底的 500 分支。
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleDataIntegrity(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        ResultCode resultCode = databaseErrorCode(exception)
                .map(this::toResultCodeFromDatabase)
                .orElse(ResultCode.BAD_REQUEST);
        String message = switch (resultCode) {
            case CONFLICT -> "数据已存在，违反唯一性约束";
            case NOT_FOUND -> resultCode.message() + "：引用的关联数据可能已被删除";
            default -> resultCode.message();
        };
        log.warn("数据完整性异常 {} {} -> {}", request.getMethod(), request.getRequestURI(),
                rootMessage(exception));
        return build(resultCode, message, request, List.of());
    }

    /** 业务层主动抛出的状态异常，取其中的原因作为提示文案。 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleResponseStatus(
            ResponseStatusException exception, HttpServletRequest request) {
        ResultCode resultCode = toResultCode(exception.getStatusCode().value());
        String message = exception.getReason() == null ? resultCode.message() : exception.getReason();
        return build(resultCode, message, request, List.of());
    }

    /** 请求路径不存在。 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleNoResource(
            NoResourceFoundException exception, HttpServletRequest request) {
        return build(ResultCode.NOT_FOUND, "请求的接口不存在: " + exception.getResourcePath(), request, List.of());
    }

    /** 请求方法不支持。 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        return build(ResultCode.BAD_REQUEST, "请求方法不支持: " + exception.getMethod(), request, List.of());
    }

    /** 兜底：记录堆栈，响应体不暴露内部实现细节。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<List<ErrorField>>> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        log.error("未处理异常 {} {}", request.getMethod(), request.getRequestURI(), exception);
        return build(ResultCode.INTERNAL_ERROR, ResultCode.INTERNAL_ERROR.message(), request, List.of());
    }

    private ResponseEntity<ApiResponse<List<ErrorField>>> build(
            ResultCode resultCode, String message, HttpServletRequest request, List<ErrorField> fields) {
        ApiResponse<List<ErrorField>> body = ApiResponse.failure(
                resultCode.code(), message, request.getRequestURI(), fields);
        return ResponseEntity.status(resultCode.httpStatus()).body(body);
    }

    /** 把任意 HTTP 状态码映射到统一的业务状态码。 */
    private ResultCode toResultCode(int status) {
        return switch (status) {
            case 200 -> ResultCode.SUCCESS;
            case 400 -> ResultCode.BAD_REQUEST;
            case 401 -> ResultCode.UNAUTHORIZED;
            case 403 -> ResultCode.FORBIDDEN;
            case 404 -> ResultCode.NOT_FOUND;
            case 409 -> ResultCode.CONFLICT;
            default -> status >= 500 ? ResultCode.INTERNAL_ERROR : ResultCode.BAD_REQUEST;
        };
    }

    /** 从最内层的 SQLException 中取出数据库错误码。 */
    private Optional<Integer> databaseErrorCode(DataIntegrityViolationException exception) {
        Throwable cause = exception.getMostSpecificCause();
        return cause instanceof SQLException sqlException
                ? Optional.of(sqlException.getErrorCode())
                : Optional.empty();
    }

    /** 数据库错误码映射到统一的业务状态码。 */
    private ResultCode toResultCodeFromDatabase(int databaseErrorCode) {
        return switch (databaseErrorCode) {
            case SQL_DUPLICATE_ENTRY -> ResultCode.CONFLICT;
            case SQL_FOREIGN_KEY_CHILD, SQL_FOREIGN_KEY_PARENT -> ResultCode.NOT_FOUND;
            default -> ResultCode.BAD_REQUEST;
        };
    }

    private String resolveMessage(FieldError error) {
        return error.getDefaultMessage() == null ? "字段取值不合法" : error.getDefaultMessage();
    }

    private String lastNode(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath() == null ? "" : violation.getPropertyPath().toString();
        int separator = path.lastIndexOf('.');
        return separator < 0 ? path : path.substring(separator + 1);
    }

    /** 只保留最内层异常信息，避免把整条 SQL 拼进日志。 */
    private String rootMessage(DataIntegrityViolationException exception) {
        return Objects.toString(exception.getMostSpecificCause().getMessage(), exception.getMessage());
    }

    /** 单个字段的校验失败详情。 */
    public record ErrorField(String field, String message) {
    }
}
