package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 彩票投资主记录表 lottery_record（核心）
 * 一个用户一天一条，存储当日聚合数据
 * 对应文档：5.数据库表结构设计.md 2.3
 */
@Data
@TableName("lottery_record")
public class LotteryRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate recordDate;

    /** 当日投入合计 */
    private BigDecimal investAmount;

    /** 当日收益合计 */
    private BigDecimal profitAmount;

    /** 当日净收益（盈亏） */
    private BigDecimal netAmount;

    /** 收益率 = net_amount / invest_amount * 100% */
    private BigDecimal profitRate;

    /** 盈亏状态：1盈利 0持平 -1亏损 */
    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
