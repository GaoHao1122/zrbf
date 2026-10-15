package com.zaori.baofu.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 查询每日记录响应
 * 对应接口：GET /api/v1/lottery/detail/{date}
 * 对应文档：6.接口设计.md 4.2 返回
 */
@Data
public class DailyRecordResponse {

    private LocalDate date;
    private Long recordId;
    private BigDecimal investAmount;
    private BigDecimal profitAmount;
    private BigDecimal netAmount;
    private String profitRate;
    private String status;
    private Integer detailCount;
    private List<DetailItem> details;

    @Data
    public static class DetailItem {
        private Long detailId;
        private Long lotteryTypeId;
        private String lotteryTypeName;
        private BigDecimal ticketAmount;
        private BigDecimal winAmount;
        private BigDecimal netAmount;
        private String remark;
    }
}
