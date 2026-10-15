package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 彩票类型字典表 lottery_type
 * 对应文档：5.数据库表结构设计.md 2.2
 */
@Data
@TableName("lottery_type")
public class LotteryType implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
