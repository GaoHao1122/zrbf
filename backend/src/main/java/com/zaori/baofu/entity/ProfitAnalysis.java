package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 盈亏分析表 profit_analysis
 * 对应文档：5.数据库表结构设计.md 7
 * 结构：record_id 关联主记录，存储当日分析结果
 */
@Data
@TableName("profit_analysis")
public class ProfitAnalysis implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 lottery_record.id */
    private Long recordId;

    /** 盈亏状态：1盈利 0持平 -1亏损 */
    private Integer profitStatus;

    /** 收益率 */
    private BigDecimal profitRate;

    /** 分析描述 */
    private String analysisText;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
