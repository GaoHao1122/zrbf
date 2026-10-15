package com.zaori.baofu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增彩票明细请求
 * 对应接口：POST /api/v1/lottery
 * 对应文档：6.接口设计.md 4.1
 */
@Data
public class LotteryDetailAddRequest {

    @NotNull(message = "日期不能为空")
    private LocalDate date;

    @NotNull(message = "彩票类型ID不能为空")
    private Long lotteryTypeId;

    @NotNull(message = "投入本金不能为空")
    @DecimalMin(value = "0", message = "投入本金必须>=0")
    @DecimalMax(value = "9999999.99", message = "金额超出合理范围")
    private BigDecimal ticketAmount;

    @NotNull(message = "中奖金额不能为空")
    @DecimalMin(value = "0", message = "中奖金额必须>=0")
    @DecimalMax(value = "9999999.99", message = "金额超出合理范围")
    private BigDecimal winAmount;

    @Size(max = 100, message = "备注最多 100 字符")
    private String remark;
}
