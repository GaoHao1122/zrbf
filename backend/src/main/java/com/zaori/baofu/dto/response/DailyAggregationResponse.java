package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 当日聚合数据
 * 用于新增明细返回中的 dailyAggregation 字段
 */
@Data
public class DailyAggregationResponse {

    private BigDecimal investAmount;
    private BigDecimal profitAmount;
    private BigDecimal netAmount;
    private String profitRate;
    private String status;
}
