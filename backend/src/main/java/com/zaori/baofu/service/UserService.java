package com.zaori.baofu.service;

import com.zaori.baofu.dto.request.WxLoginRequest;
import com.zaori.baofu.dto.response.LoginResponse;
import com.zaori.baofu.dto.response.UserInfoResponse;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 微信登录
     */
    LoginResponse login(WxLoginRequest request);

    /**
     * 获取用户信息（含累计统计）
     */
    UserInfoResponse getProfile(Long userId);
}
