package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.common.UserContext;
import com.zaori.baofu.dto.request.WxLoginRequest;
import com.zaori.baofu.dto.response.LoginResponse;
import com.zaori.baofu.dto.response.UserInfoResponse;
import com.zaori.baofu.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块
 * 对应文档：6.接口设计.md 3 用户模块
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 3.1 微信登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody WxLoginRequest request) {
        return Result.success(userService.login(request));
    }

    /**
     * 3.2 获取用户信息
     */
    @GetMapping("/profile")
    public Result<UserInfoResponse> profile() {
        Long userId = getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
