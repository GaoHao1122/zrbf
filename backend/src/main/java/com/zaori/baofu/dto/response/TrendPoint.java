package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 趋势图数据点
 * 对应接口：GET /api/v1/chart/trend
 * 对应文档：6.接口设计.md 7.1
 */
@Data
public class TrendPoint {

    private String x;
    private BigDecimal y;
}
