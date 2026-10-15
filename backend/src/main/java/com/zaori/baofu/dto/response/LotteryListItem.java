package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 历史记录列表项
 * 对应接口：GET /api/v1/lottery/list
 * 对应文档：6.接口设计.md 4.3 返回
 */
@Data
public class LotteryListItem {

    private Long recordId;
    private LocalDate date;
    private BigDecimal investAmount;
    private BigDecimal profitAmount;
    private BigDecimal netAmount;
    private String profitRate;
    private String status;
    private Integer detailCount;
}
