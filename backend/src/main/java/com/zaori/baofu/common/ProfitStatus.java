package com.zaori.baofu.common;

/**
 * 盈亏状态常量
 * 对应文档：5.数据库表结构设计.md 4.3 状态值定义
 * DB: 1/0/-1
 * 接口: PROFIT/EVEN/LOSS
 */
public class ProfitStatus {

    /** 数据库存储值 */
    public static final int DB_PROFIT = 1;
    public static final int DB_EVEN = 0;
    public static final int DB_LOSS = -1;

    /** 接口返回值 */
    public static final String API_PROFIT = "PROFIT";
    public static final String API_EVEN = "EVEN";
    public static final String API_LOSS = "LOSS";

    /**
     * 数据库状态值转接口字符串
     */
    public static String dbToApi(int dbStatus) {
        return switch (dbStatus) {
            case DB_PROFIT -> API_PROFIT;
            case DB_EVEN -> API_EVEN;
            case DB_LOSS -> API_LOSS;
            default -> API_EVEN;
        };
    }

    /**
     * 根据 netAmount 判断数据库状态值
     */
    public static int fromNetAmount(java.math.BigDecimal netAmount) {
        int cmp = netAmount.compareTo(java.math.BigDecimal.ZERO);
        if (cmp > 0) return DB_PROFIT;
        if (cmp < 0) return DB_LOSS;
        return DB_EVEN;
    }
}
