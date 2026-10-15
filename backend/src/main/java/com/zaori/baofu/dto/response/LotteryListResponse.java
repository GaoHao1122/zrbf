package com.zaori.baofu.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 历史记录列表响应
 * 对应接口：GET /api/v1/lottery/list
 * 对应文档：6.接口设计.md 4.3 返回 {total, list}
 */
@Data
public class LotteryListResponse {

    private Long total;
    private List<LotteryListItem> list;
}
