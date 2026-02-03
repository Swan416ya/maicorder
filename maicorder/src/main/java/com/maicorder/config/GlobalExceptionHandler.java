package com.maicorder.config;

import com.maicorder.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（RESTful接口异常统一响应）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 通用运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.fail(500, "系统异常：" + e.getMessage());
    }

    // 空指针异常
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNPE(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.fail(500, "系统异常：空指针错误");
    }

    // 所有异常兜底
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.fail(500, "服务器内部错误，请联系管理员");
    }
}