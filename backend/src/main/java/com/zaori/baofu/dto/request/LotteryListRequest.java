package com.zaori.baofu.dto.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * 历史记录列表查询请求
 * 对应接口：GET /api/v1/lottery/list
 * 对应文档：6.接口设计.md 4.3
 */
@Data
public class LotteryListRequest {

    private Integer page = 1;

    private Integer size = 20;

    private LocalDate startDate;

    private LocalDate endDate;
}
