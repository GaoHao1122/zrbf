package com.zaori.baofu.dto.request;

import lombok.Data;

/**
 * 图表趋势查询请求
 * 对应接口：GET /api/v1/chart/trend
 * 对应文档：6.接口设计.md 7.1
 */
@Data
public class ChartTrendRequest {

    /**
     * 类型：day/week/month/year
     */
    private String type = "week";
}
