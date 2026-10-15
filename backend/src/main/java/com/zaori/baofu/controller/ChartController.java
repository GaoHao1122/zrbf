package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.common.UserContext;
import com.zaori.baofu.dto.request.ChartTrendRequest;
import com.zaori.baofu.dto.response.TrendPoint;
import com.zaori.baofu.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图表模块
 * 对应文档：6.接口设计.md 7 图表接口设计
 */
@RestController
@RequestMapping("/api/v1/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    /**
     * 7.1 趋势曲线
     */
    @GetMapping("/trend")
    public Result<List<TrendPoint>> trend(ChartTrendRequest request) {
        Long userId = getCurrentUserId();
        return Result.success(chartService.getTrend(userId, request));
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
