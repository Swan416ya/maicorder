package com.maicorder.common;

import lombok.Data;

/**
 * RESTful统一响应格式
 */
@Data
public class Result<T> {
    // 响应码：200成功/400参数错/401未认证/500系统错
    private Integer code;
    // 响应信息
    private String message;
    // 响应数据
    private T data;

    // 静态构造方法（简化调用）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    // 快捷方法：认证失败（401）
    public static <T> Result<T> unauthorized(String message) {
        return fail(401, message);
    }

    // 快捷方法：参数错误（400）
    public static <T> Result<T> badRequest(String message) {
        return fail(400, message);
    }
}