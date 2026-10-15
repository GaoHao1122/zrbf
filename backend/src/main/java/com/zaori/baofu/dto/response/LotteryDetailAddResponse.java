package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增彩票明细响应
 * 对应接口：POST /api/v1/lottery
 * 对应文档：6.接口设计.md 4.1 返回
 */
@Data
public class LotteryDetailAddResponse {

    private Long detailId;
    private Long recordId;
    private BigDecimal ticketAmount;
    private BigDecimal winAmount;
    private BigDecimal netAmount;
    private String status;
    private DailyAggregationResponse dailyAggregation;
}
