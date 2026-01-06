package com.zk.petclinic.exception;

import com.zk.petclinic.util.ResultUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各类异常，返回规范的错误响应
 * @author 张恺
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResultUtil<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultUtil.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid/@Validated 注解校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultUtil<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ResultUtil.fail(400, message);
    }

    /**
     * 处理约束违反异常（@NotNull、@Size等注解校验失败）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultUtil<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return ResultUtil.fail(400, message);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultUtil<Void> handleMissingParamException(MissingServletRequestParameterException e) {
        String message = "缺少必要参数: " + e.getParameterName();
        log.warn(message);
        return ResultUtil.fail(400, message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultUtil<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "参数类型错误: " + e.getName();
        log.warn(message);
        return ResultUtil.fail(400, message);
    }

    /**
     * 处理请求体解析失败异常（JSON格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultUtil<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return ResultUtil.fail(400, "请求数据格式错误");
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultUtil<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方法: " + e.getMethod();
        log.warn(message);
        return ResultUtil.fail(405, message);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultUtil<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getRequestURL());
        return ResultUtil.fail(404, "接口不存在");
    }

    /**
     * 处理文件上传过大异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultUtil<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("上传文件过大: {}", e.getMessage());
        return ResultUtil.fail(400, "上传文件过大，请压缩后重试");
    }

    /**
     * 处理认证异常（未登录）
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResultUtil<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResultUtil.fail(401, "请先登录");
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResultUtil<Void> handleAccessDenied(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ResultUtil.fail(403, "权限不足，无法访问该资源");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResultUtil<Void> handleNullPointer(NullPointerException e) {
        log.error("空指针异常", e);
        return ResultUtil.fail(500, "系统异常，请稍后重试");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultUtil<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return ResultUtil.fail(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultUtil<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return ResultUtil.fail(500, "系统繁忙，请稍后重试");
    }

    /**
     * 处理所有其他异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public ResultUtil<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResultUtil.fail(500, "系统异常，请联系管理员");
    }
}
