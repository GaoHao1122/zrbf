package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户信息响应
 * 对应接口：GET /api/v1/user/profile
 */
@Data
public class UserInfoResponse {

    private Long id;
    private String nickname;
    private String avatar;
    private BigDecimal totalInvestment;
    private BigDecimal totalProfit;
    private BigDecimal totalLoss;
}
