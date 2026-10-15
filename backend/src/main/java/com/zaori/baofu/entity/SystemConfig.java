package com.zaori.baofu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置表 system_config
 * 对应文档：5.数据库表结构设计.md 2.6
 */
@Data
@TableName("system_config")
public class SystemConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String configKey;

    private String configValue;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
