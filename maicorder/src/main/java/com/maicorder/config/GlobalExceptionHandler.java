package com.maicorder.config;

import com.maicorder.common.Result;
import org.slf4j.Logger;  // 导入SLF4J的Logger
import org.slf4j.LoggerFactory; // 导入LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（无Lombok版本）
 */
@RestControllerAdvice // 全局捕获Controller层异常
public class GlobalExceptionHandler {

    // 手动创建日志对象（替代Lombok的@Slf4j）
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 数字格式转换异常（如非数字转Long/Double）
    @ExceptionHandler(NumberFormatException.class)
    public Result<Void> handleNumberFormatException(NumberFormatException e) {
        log.error("参数格式错误", e); // 正常使用log
        return Result.fail(500, "参数格式错误：请输入有效的数字");
    }

    // 日期解析异常
    @ExceptionHandler(java.time.format.DateTimeParseException.class)
    public Result<Void> handleDateTimeParseException(java.time.format.DateTimeParseException e) {
        log.error("日期解析失败", e);
        return Result.fail(500, "日期格式错误：请使用 yyyy-MM-dd 格式");
    }

    // 运行时异常（通用）
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.fail(500, "系统异常：" + e.getMessage());
    }

    // 兜底捕获所有异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return Result.fail(500, "服务器内部错误，请联系管理员");
    }
}