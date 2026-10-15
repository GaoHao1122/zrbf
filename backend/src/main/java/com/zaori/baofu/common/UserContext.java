package com.zaori.baofu.common;

/**
 * 当前登录用户上下文。
 * 登录拦截器解析 JWT 后把 userId 放进来，Controller 直接读取。
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        Long userId = USER_ID.get();
        return userId == null ? 0L : userId;
    }

    public static void clear() {
        USER_ID.remove();
    }
}
