package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 首页 Dashboard 响应
 * 对应接口：GET /api/v1/dashboard
 * 对应文档：6.接口设计.md 6
 */
@Data
public class DashboardResponse {

    private TodayInfo today;
    private SummaryInfo summary;
    private ChartInfo chart;

    @Data
    public static class TodayInfo {
        private BigDecimal investAmount;
        private BigDecimal netAmount;
        private String profitRate;
        private String status;
    }

    @Data
    public static class SummaryInfo {
        private BigDecimal totalInvestAmount;
        private BigDecimal totalProfitAmount;
        private BigDecimal totalNetAmount;
        private String winRate;
    }

    @Data
    public static class ChartInfo {
        private String type;
        private java.util.List<ChartPoint> data;
    }

    @Data
    public static class ChartPoint {
        private String x;
        private BigDecimal y;
    }
}
