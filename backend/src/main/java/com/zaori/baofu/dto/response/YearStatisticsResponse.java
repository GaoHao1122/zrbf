package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 年度统计响应
 * 对应接口：GET /api/v1/statistics/year
 * 对应文档：6.接口设计.md 5.4
 */
@Data
public class YearStatisticsResponse {

    private Integer year;
    private List<MonthPoint> chart;

    @Data
    public static class MonthPoint {
        private String date;
        private BigDecimal netAmount;
    }
}
