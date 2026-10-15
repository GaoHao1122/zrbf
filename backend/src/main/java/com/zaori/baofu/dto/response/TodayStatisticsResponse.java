package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 今日盈亏响应
 * 对应接口：GET /api/v1/statistics/today
 * 对应文档：6.接口设计.md 5.1
 */
@Data
public class TodayStatisticsResponse {

    private LocalDate date;
    private BigDecimal investAmount;
    private BigDecimal profitAmount;
    private BigDecimal netAmount;
    private String profitRate;
    private String status;
}
