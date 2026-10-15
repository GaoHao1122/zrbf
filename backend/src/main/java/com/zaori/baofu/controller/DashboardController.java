package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.common.UserContext;
import com.zaori.baofu.dto.response.DashboardResponse;
import com.zaori.baofu.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 首页 Dashboard 模块
 * 对应文档：6.接口设计.md 6 首页 Dashboard 接口
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 6.1 首页数据
     */
    @GetMapping
    public Result<DashboardResponse> dashboard() {
        Long userId = getCurrentUserId();
        return Result.success(dashboardService.getDashboard(userId));
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
