package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月统计响应
 * 对应接口：GET /api/v1/statistics/month
 * 对应文档：6.接口设计.md 5.3
 * chart 字段：date（日期字符串，如 08-01）
 */
@Data
public class MonthStatisticsResponse {

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
