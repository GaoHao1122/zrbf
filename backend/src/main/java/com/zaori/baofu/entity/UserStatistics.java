package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户收益统计表 user_statistics
 * 由定时任务每日凌晨写入，提高首页图表查询速度
 * 对应文档：5.数据库表结构设计.md 2.5
 */
@Data
@TableName("user_statistics")
public class UserStatistics implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate statisticsDate;

    private BigDecimal dailyInvest;

    private BigDecimal dailyProfit;

    private BigDecimal dailyNet;

    private BigDecimal weekNet;

    private BigDecimal monthNet;

    private BigDecimal yearNet;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
