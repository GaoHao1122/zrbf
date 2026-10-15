package com.zaori.baofu.service;

import com.zaori.baofu.dto.request.ChartTrendRequest;
import com.zaori.baofu.dto.response.TrendPoint;

import java.util.List;

/**
 * 图表服务
 */
public interface ChartService {

    /**
     * 趋势曲线
     * @param type day/week/month/year
     */
    List<TrendPoint> getTrend(Long userId, ChartTrendRequest request);
}
