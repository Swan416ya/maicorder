package com.maicorder.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * RESTful 统一响应体（泛型）
 * 符合 RESTful 规范的标准化响应格式，包含时间戳、状态码、错误信息和业务数据
 *
 * @param <T> 业务数据的类型，支持泛型参数化
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 空字段不序列化
public class ApiResponse<T> {
    /**
     * 请求时间戳
     * 使用 UTC+8 时区的偏移时间对象，记录请求发生的准确时间
     */
    private OffsetDateTime timestamp;
    /**
     * HTTP 状态码
     * 标识请求的处理结果状态，如 200 成功、400 客户端错误、500 服务器错误等
     */
    private int status;
    /**
     * 业务错误码
     * 细分业务层面的错误类型，用于精确定位问题原因
     */
    private String errorCode;
    /**
     * 用户友好的错误消息
     * 向终端用户展示的错误描述信息，语言通俗易懂
     */
    private String message;
    /**
     * 请求路径
     * 当前请求的 API 接口路径，用于标识具体的接口地址
     */
    private String path;
    /**
     * 业务响应数据
     * 请求成功时返回的具体业务数据，类型为泛型参数 T
     */
    private T data;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 构建成功的响应对象
     * 使用默认的成功状态码和消息，快速创建标准成功响应
     *
     * @param path String 请求的 API 接口路径
     * @param data T 业务响应数据，类型为泛型参数 T
     * @return ApiResponse&lt;T&gt; 填充完整成功信息的响应对象，时间戳为 UTC+8 时区
     */
    public static <T> ApiResponse<T> success(String path, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setTimestamp( OffsetDateTime.now(ZoneOffset.ofHours(8)));
        response.setStatus(HttpStatus.OK.value());
        response.setErrorCode("00000"); // 成功码（行业通用：00000表示成功）
        response.setMessage("操作成功");
        response.setPath(path);
        response.setData(data);
        return response;
    }

    /**
     * 构建失败的响应对象
     * 根据指定的 HTTP 状态码、错误码和消息创建标准错误响应
     *
     * @param path String 请求的 API 接口路径
     * @param httpStatus HttpStatus HTTP 状态码，决定响应的 HTTP 状态
     * @param errorCode String 业务错误码，用于精确定位错误类型
     * @param message String 用户友好的错误描述信息
     * @return ApiResponse&lt;T&gt; 填充完整失败信息的响应对象，时间戳为 UTC+8 时区
     */
    public static <T> ApiResponse<T> fail(String path, HttpStatus httpStatus, String errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setTimestamp( OffsetDateTime.now(ZoneOffset.ofHours(8)));
        response.setStatus(httpStatus.value());
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }
}
