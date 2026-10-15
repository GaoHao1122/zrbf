package com.zaori.baofu.service;

import com.zaori.baofu.dto.response.MonthStatisticsResponse;
import com.zaori.baofu.dto.response.TodayStatisticsResponse;
import com.zaori.baofu.dto.response.WeekStatisticsResponse;
import com.zaori.baofu.dto.response.YearStatisticsResponse;

/**
 * 统计服务
 *
 * 数据来源优先级：
 * 1) 今日数据：实时从 lottery_record 取
 * 2) 周/月/年数据：当前版本实时从 lottery_record 聚合计算
 */
public interface StatisticsService {

    TodayStatisticsResponse getToday(Long userId);

    WeekStatisticsResponse getWeek(Long userId);

    MonthStatisticsResponse getMonth(Long userId, Integer year, Integer month);

    YearStatisticsResponse getYear(Long userId, Integer year);
}
