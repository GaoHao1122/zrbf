-- ============================================================
-- 早日暴富小程序 数据库建表脚本 V1.2
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- 对应文档：5.数据库表结构设计.md V1.2
-- 生成日期：2026-08-11
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS zrbf
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE zrbf;

-- ============================================================
-- 1. 用户表 sys_user
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT          COMMENT '主键',
    openid      VARCHAR(64)  NOT NULL                            COMMENT '微信openid',
    unionid     VARCHAR(64)                                      COMMENT '微信unionid',
    nickname    VARCHAR(64)                                      COMMENT '用户昵称',
    avatar_url  VARCHAR(255)                                     COMMENT '头像URL',
    phone       VARCHAR(20)                                      COMMENT '手机号',
    status      TINYINT       DEFAULT 1                          COMMENT '状态：1正常 0禁用',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP          COMMENT '创建时间',
    update_time DATETIME      DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP        COMMENT '更新时间',
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 彩票类型字典表 lottery_type
-- ============================================================
DROP TABLE IF EXISTS lottery_type;
CREATE TABLE lottery_type (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT          COMMENT '主键',
    name        VARCHAR(50)  NOT NULL                            COMMENT '彩票名称',
    code        VARCHAR(30)  NOT NULL                            COMMENT '彩票编码',
    status      TINYINT       DEFAULT 1                          COMMENT '状态：1启用 0禁用',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP          COMMENT '创建时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='彩票类型字典表';

-- 初始数据
INSERT INTO lottery_type (id, name, code) VALUES
    (1, '大乐透', 'DLT'),
    (2, '排列三', 'PL3'),
    (3, '排列五', 'PL5'),
    (4, '七星彩', 'QXC');

-- ============================================================
-- 3. 彩票投资主记录表 lottery_record（核心）
-- 一个用户一天一条，存储当日聚合数据
-- 由 lottery_detail 明细变更时实时聚合更新
-- ============================================================
DROP TABLE IF EXISTS lottery_record;
CREATE TABLE lottery_record (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT        COMMENT '主键',
    user_id       BIGINT        NOT NULL                          COMMENT '用户ID',
    record_date   DATE          NOT NULL                          COMMENT '投资日期',
    invest_amount DECIMAL(10,2) DEFAULT 0                         COMMENT '当日投入合计',
    profit_amount DECIMAL(10,2) DEFAULT 0                         COMMENT '当日收益合计',
    net_amount    DECIMAL(10,2)                                   COMMENT '当日净收益（盈利为正，亏损为负）',
    profit_rate   DECIMAL(8,2)                                    COMMENT '收益率 = net_amount / invest_amount * 100%',
    status        TINYINT                                         COMMENT '盈亏状态：1盈利 0持平 -1亏损',
    remark        VARCHAR(255)                                    COMMENT '备注',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP         COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP       COMMENT '更新时间',
    UNIQUE KEY uk_user_date (user_id, record_date)                COMMENT '唯一约束：一个用户一天只有一条主记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='彩票投资主记录表（当日聚合）';

-- ============================================================
-- 4. 彩票明细表 lottery_detail（核心）
-- 一条主记录对应多条明细，每条明细对应一种彩票的一次投注
-- ============================================================
DROP TABLE IF EXISTS lottery_detail;
CREATE TABLE lottery_detail (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT       COMMENT '主键',
    record_id       BIGINT        NOT NULL                         COMMENT '关联 lottery_record.id',
    user_id         BIGINT        NOT NULL                         COMMENT '用户ID（冗余便于查询）',
    lottery_type_id BIGINT        NOT NULL                         COMMENT '彩票类型ID（关联 lottery_type.id）',
    ticket_amount   DECIMAL(10,2) NOT NULL DEFAULT 0               COMMENT '该笔投入本金',
    win_amount      DECIMAL(10,2) NOT NULL DEFAULT 0               COMMENT '该笔最终获利',
    net_amount      DECIMAL(10,2)                                  COMMENT '该笔净收益（盈利为正，亏损为负）',
    remark          VARCHAR(255)                                   COMMENT '备注',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
    update_time     DATETIME      DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP      COMMENT '更新时间',
    INDEX idx_record    (record_id)                                COMMENT '按主记录查询明细列表',
    INDEX idx_user_date (user_id, create_time)                     COMMENT '按用户跨日查询明细',
    INDEX idx_type      (lottery_type_id)                          COMMENT '按彩票类型筛选'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='彩票明细表（每笔投注）';

-- ============================================================
-- 5. 用户收益统计表 user_statistics
-- 由定时任务每日凌晨写入，提高首页图表查询速度
-- ============================================================
DROP TABLE IF EXISTS user_statistics;
CREATE TABLE user_statistics (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT       COMMENT '主键',
    user_id         BIGINT        NOT NULL                         COMMENT '用户ID',
    statistics_date DATE          NOT NULL                         COMMENT '统计日期',
    daily_invest    DECIMAL(10,2)                                  COMMENT '每日投入',
    daily_profit    DECIMAL(10,2)                                  COMMENT '每日盈利',
    daily_net       DECIMAL(10,2)                                  COMMENT '每日净收益',
    week_net        DECIMAL(10,2)                                  COMMENT '周收益',
    month_net       DECIMAL(10,2)                                  COMMENT '月收益',
    year_net        DECIMAL(10,2)                                  COMMENT '年收益',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
    UNIQUE KEY uk_user_date (user_id, statistics_date)             COMMENT '唯一约束：一个用户一天只有一条统计记录'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收益统计表';

-- ============================================================
-- 6. 系统配置表 system_config
-- ============================================================
DROP TABLE IF EXISTS system_config;
CREATE TABLE system_config (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT           COMMENT '主键',
    config_key   VARCHAR(100) NOT NULL                             COMMENT '配置key',
    config_value VARCHAR(500)                                      COMMENT '配置值',
    remark       VARCHAR(255)                                      COMMENT '说明',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ============================================================
-- 7. 盈亏分析表 profit_analysis（预留扩展）
-- 对应文档：4.数据库ER模型.md 2.5
-- 结构：record_id 关联主记录，存储当日分析结果
-- ============================================================
DROP TABLE IF EXISTS profit_analysis;
CREATE TABLE profit_analysis (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT       COMMENT '主键',
    record_id       BIGINT        NOT NULL                         COMMENT '关联 lottery_record.id',
    profit_status   TINYINT                                        COMMENT '盈亏状态：1盈利 0持平 -1亏损',
    profit_rate     DECIMAL(5,2)                                   COMMENT '收益率',
    analysis_text   VARCHAR(255)                                   COMMENT '分析描述',
    create_time     DATETIME      DEFAULT CURRENT_TIMESTAMP        COMMENT '创建时间',
    INDEX idx_record (record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盈亏分析表';

-- ============================================================
-- 8. 操作日志表 operation_log
-- ============================================================
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT           COMMENT '主键',
    user_id     BIGINT       NOT NULL                             COMMENT '用户ID',
    operation   VARCHAR(50)  NOT NULL                             COMMENT '操作类型',
    content     TEXT                                              COMMENT '操作内容',
    ip          VARCHAR(50)                                       COMMENT 'IP地址',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
    INDEX idx_user (user_id),
    INDEX idx_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================================
-- 建表完成
-- 表清单：
--   1. sys_user          用户表
--   2. lottery_type      彩票类型字典表（含初始数据）
--   3. lottery_record    彩票投资主记录表（当日聚合）
--   4. lottery_detail    彩票明细表（每笔投注）
--   5. user_statistics   用户收益统计表
--   6. system_config     系统配置表
--   7. profit_analysis   盈亏分析表（预留扩展）
--   8. operation_log     操作日志表
-- ============================================================
