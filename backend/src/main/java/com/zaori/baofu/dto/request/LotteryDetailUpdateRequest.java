package com.zaori.baofu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改彩票明细请求
 * 对应接口：PUT /api/v1/lottery/detail/{id}
 * 对应文档：6.接口设计.md 4.4
 */
@Data
public class LotteryDetailUpdateRequest {

    private Long lotteryTypeId;

    @DecimalMin(value = "0", message = "投入本金必须>=0")
    @DecimalMax(value = "9999999.99", message = "金额超出合理范围")
    private BigDecimal ticketAmount;

    @DecimalMin(value = "0", message = "中奖金额必须>=0")
    @DecimalMax(value = "9999999.99", message = "金额超出合理范围")
    private BigDecimal winAmount;

    @Size(max = 100, message = "备注最多 100 字符")
    private String remark;
}
