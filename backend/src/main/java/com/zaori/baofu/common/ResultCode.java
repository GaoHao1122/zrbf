package com.zaori.baofu.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码
 * 对应文档：6.接口设计.md 2.3 状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "数据不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    SERVER_ERROR(500, "服务器异常");

    private final Integer code;
    private final String message;
}
