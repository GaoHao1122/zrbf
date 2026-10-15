package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台模块（预留）
 * 对应文档：6.接口设计.md 8 管理后台接口
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 8.1 用户列表
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> users(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(adminService.listUsers(page, size));
    }

    /**
     * 8.2 全站统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.success(adminService.getGlobalStatistics());
    }
}
