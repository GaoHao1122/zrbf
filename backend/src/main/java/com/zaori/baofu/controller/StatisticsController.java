package com.zaori.baofu.controller;

import com.zaori.baofu.common.Result;
import com.zaori.baofu.common.UserContext;
import com.zaori.baofu.dto.response.MonthStatisticsResponse;
import com.zaori.baofu.dto.response.TodayStatisticsResponse;
import com.zaori.baofu.dto.response.WeekStatisticsResponse;
import com.zaori.baofu.dto.response.YearStatisticsResponse;
import com.zaori.baofu.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 统计模块
 * 对应文档：6.接口设计.md 5 统计数据
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 5.1 今日盈亏
     */
    @GetMapping("/today")
    public Result<TodayStatisticsResponse> today() {
        Long userId = getCurrentUserId();
        return Result.success(statisticsService.getToday(userId));
    }

    /**
     * 5.2 周统计
     */
    @GetMapping("/week")
    public Result<WeekStatisticsResponse> week() {
        Long userId = getCurrentUserId();
        return Result.success(statisticsService.getWeek(userId));
    }

    /**
     * 5.3 月统计
     */
    @GetMapping("/month")
    public Result<MonthStatisticsResponse> month(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        Long userId = getCurrentUserId();
        return Result.success(statisticsService.getMonth(userId, year, month));
    }

    /**
     * 5.4 年度统计
     */
    @GetMapping("/year")
    public Result<YearStatisticsResponse> year(
            @RequestParam(required = false) Integer year) {
        Long userId = getCurrentUserId();
        return Result.success(statisticsService.getYear(userId, year));
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
