package com.maicorder.entity;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * API 请求统一封装类（泛型）
 * 用于标准化请求和响应的数据格式，包含时间戳、消息和业务数据
 *
 * @param <T> 业务数据的类型，支持泛型参数化
 */
@Data
public class ApiRequest<T> {
    /**
     * 请求时间戳
     * 使用带时区偏移量的时间对象（UTC+8），记录请求发生的准确时间
     */
    private OffsetDateTime timestamp;
    /**
     * 响应消息
     * 向用户展示的操作结果描述信息，成功或失败时的友好提示
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
}