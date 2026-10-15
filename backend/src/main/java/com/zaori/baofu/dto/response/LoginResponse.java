package com.zaori.baofu.dto.response;

import lombok.Data;

/**
 * 登录响应
 * 对应接口：POST /api/v1/user/login
 */
@Data
public class LoginResponse {

    private String token;
    private UserInfoResponse user;
}
