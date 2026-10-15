package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 周统计响应
 * 对应接口：GET /api/v1/statistics/week
 * 对应文档：6.接口设计.md 5.2
 * chart 字段：date（日期字符串）
 */
@Data
public class WeekStatisticsResponse {

    private BigDecimal totalInvestAmount;
    private BigDecimal totalProfitAmount;
    private BigDecimal totalNetAmount;
    private List<ChartPoint> chart;

    @Data
    public static class ChartPoint {
        private String date;
        private BigDecimal netAmount;
    }
}
