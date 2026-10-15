package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 彩票明细表 lottery_detail（核心）
 * 一条主记录对应多条明细，每条明细对应一种彩票的一次投注
 * 对应文档：5.数据库表结构设计.md 2.4
 */
@Data
@TableName("lottery_detail")
public class LotteryDetail implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 lottery_record.id */
    private Long recordId;

    /** 用户ID（冗余便于查询） */
    private Long userId;

    /** 彩票类型ID（关联 lottery_type.id） */
    private Long lotteryTypeId;

    /** 该笔投入本金 */
    private BigDecimal ticketAmount;

    /** 该笔最终获利 */
    private BigDecimal winAmount;

    /** 该笔净收益 = win_amount - ticket_amount */
    private BigDecimal netAmount;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
