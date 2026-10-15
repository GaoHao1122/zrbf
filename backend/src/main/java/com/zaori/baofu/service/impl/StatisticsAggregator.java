package com.zaori.baofu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zaori.baofu.dto.response.TrendPoint;
import com.zaori.baofu.entity.LotteryRecord;
import com.zaori.baofu.mapper.LotteryRecordMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计聚合工具：负责查询主记录、汇总金额、生成趋势图数据点。
 * 被统计、首页、图表、用户资料等服务复用。
 */
@Component
@RequiredArgsConstructor
public class StatisticsAggregator {

    private static final DateTimeFormatter MM_DD = DateTimeFormatter.ofPattern("MM-dd");

    private final LotteryRecordMapper lotteryRecordMapper;

    /**
     * 按用户和日期范围查询主记录，日期可为空表示不限
     */
    public List<LotteryRecord> queryRange(Long userId, LocalDate start, LocalDate end) {
        LambdaQueryWrapper<LotteryRecord> wrapper = new LambdaQueryWrapper<LotteryRecord>()
                .eq(LotteryRecord::getUserId, userId);
        if (start != null) {
            wrapper.ge(LotteryRecord::getRecordDate, start);
        }
        if (end != null) {
            wrapper.le(LotteryRecord::getRecordDate, end);
        }
        wrapper.orderByAsc(LotteryRecord::getRecordDate);
        return lotteryRecordMapper.selectList(wrapper);
    }

    /**
     * 汇总一组主记录：投入、收益、净收益、亏损额、盈利天数、胜率
     */
    public Totals aggregate(List<LotteryRecord> records) {
        Totals totals = new Totals();
        for (LotteryRecord record : records) {
            totals.investAmount = totals.investAmount.add(nz(record.getInvestAmount()));
            totals.profitAmount = totals.profitAmount.add(nz(record.getProfitAmount()));
            BigDecimal net = nz(record.getNetAmount());
            totals.netAmount = totals.netAmount.add(net);
            if (net.compareTo(BigDecimal.ZERO) > 0) {
                totals.profitDays++;
            } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                totals.lossAmount = totals.lossAmount.add(net);
            }
            totals.totalDays++;
        }
        totals.winRate = totals.totalDays == 0 ? "0%" : (totals.profitDays * 100 / totals.totalDays) + "%";
        return totals;
    }

    /**
     * 生成连续日期的趋势点，缺数据的日期补 0
     */
    public List<TrendPoint> fillDailyPoints(LocalDate start, LocalDate end, List<LotteryRecord> records, boolean fullDate) {
        Map<LocalDate, BigDecimal> byDate = records.stream()
                .collect(Collectors.toMap(LotteryRecord::getRecordDate, r -> nz(r.getNetAmount()), BigDecimal::add));

        List<TrendPoint> points = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            TrendPoint point = new TrendPoint();
            point.setX(fullDate ? date.toString() : MM_DD.format(date));
            point.setY(byDate.getOrDefault(date, BigDecimal.ZERO));
            points.add(point);
        }
        return points;
    }

    /**
     * 生成一年 12 个月的趋势点，缺数据的月份补 0
     */
    public List<TrendPoint> fillMonthlyPoints(int year, List<LotteryRecord> records, boolean fullDate) {
        Map<Integer, BigDecimal> byMonth = new HashMap<>();
        for (LotteryRecord record : records) {
            int month = record.getRecordDate().getMonthValue();
            byMonth.merge(month, nz(record.getNetAmount()), BigDecimal::add);
        }

        List<TrendPoint> points = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            TrendPoint point = new TrendPoint();
            point.setX(fullDate ? String.format("%04d-%02d", year, month) : month + "月");
            point.setY(byMonth.getOrDefault(month, BigDecimal.ZERO));
            points.add(point);
        }
        return points;
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    @Data
    public static class Totals {
        private BigDecimal investAmount = BigDecimal.ZERO;
        private BigDecimal profitAmount = BigDecimal.ZERO;
        private BigDecimal netAmount = BigDecimal.ZERO;
        private BigDecimal lossAmount = BigDecimal.ZERO;
        private int profitDays;
        private int totalDays;
        private String winRate = "0%";
    }
}
