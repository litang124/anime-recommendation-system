package com.anime.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有 Controller 抛出的异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", e.getCode());
        response.put("message", e.getMessage());
        response.put("data", null);
        
        return response;
    }

    /**
     * 处理参数验证异常（@RequestBody + @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        log.warn("参数验证异常：{}", errors);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrorCode.PARAM_ERROR.getCode());
        response.put("message", "参数验证失败：" + String.join(", ", errors));
        response.put("data", null);
        
        return response;
    }

    /**
     * 处理参数绑定异常（GET 请求参数）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBindException(BindException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        log.warn("参数绑定异常：{}", errors);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrorCode.PARAM_ERROR.getCode());
        response.put("message", "参数错误：" + String.join(", ", errors));
        response.put("data", null);
        
        return response;
    }

    /**
     * 处理 404 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NoHandlerFoundException e) {
        log.warn("资源不存在：{}", e.getRequestURL());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrorCode.DATA_NOT_FOUND.getCode());
        response.put("message", "接口不存在：" + e.getRequestURL());
        response.put("data", null);
        
        return response;
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception e) {
        log.error("系统异常：", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", ErrorCode.SYSTEM_ERROR.getCode());
        response.put("message", "系统繁忙，请稍后再试");
        response.put("data", null);
        
        // 开发环境返回详细错误信息
        /*
        if (environment.matchesProfiles("dev")) {
            response.put("error", e.getClass().getSimpleName());
            response.put("stackTrace", getStackTrace(e));
        }
        */
        
        return response;
    }

    /**
     * 获取异常堆栈信息（用于开发环境调试）
     */
    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
