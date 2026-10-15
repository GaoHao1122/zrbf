package com.zaori.baofu.service;

import com.zaori.baofu.dto.response.DashboardResponse;

/**
 * 首页 Dashboard 服务
 */
public interface DashboardService {

    DashboardResponse getDashboard(Long userId);
}
