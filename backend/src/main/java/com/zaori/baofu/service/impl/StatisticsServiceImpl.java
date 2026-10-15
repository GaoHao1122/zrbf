package com.zaori.baofu.service.impl;

import com.zaori.baofu.common.ProfitStatus;
import com.zaori.baofu.dto.response.MonthStatisticsResponse;
import com.zaori.baofu.dto.response.TodayStatisticsResponse;
import com.zaori.baofu.dto.response.WeekStatisticsResponse;
import com.zaori.baofu.dto.response.YearStatisticsResponse;
import com.zaori.baofu.entity.LotteryRecord;
import com.zaori.baofu.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsAggregator aggregator;

    @Override
    public TodayStatisticsResponse getToday(Long userId) {
        // 实时从 lottery_record 取当日数据
        LocalDate today = LocalDate.now();
        LotteryRecord record = aggregator.queryRange(userId, today, today).stream().findFirst().orElse(null);
        if (record == null) {
            return null;
        }
        TodayStatisticsResponse resp = new TodayStatisticsResponse();
        resp.setDate(record.getRecordDate());
        resp.setInvestAmount(record.getInvestAmount());
        resp.setProfitAmount(record.getProfitAmount());
        resp.setNetAmount(record.getNetAmount());
        resp.setProfitRate(record.getProfitRate() + "%");
        resp.setStatus(ProfitStatus.dbToApi(record.getStatus()));
        return resp;
    }

    @Override
    public WeekStatisticsResponse getWeek(Long userId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.with(DayOfWeek.MONDAY);
        List<LotteryRecord> records = aggregator.queryRange(userId, start, end);
        StatisticsAggregator.Totals totals = aggregator.aggregate(records);

        WeekStatisticsResponse resp = new WeekStatisticsResponse();
        resp.setTotalInvestAmount(totals.getInvestAmount());
        resp.setTotalProfitAmount(totals.getProfitAmount());
        resp.setTotalNetAmount(totals.getNetAmount());
        resp.setChart(aggregator.fillDailyPoints(start, end, records, false).stream()
                .map(point -> {
                    WeekStatisticsResponse.ChartPoint cp = new WeekStatisticsResponse.ChartPoint();
                    cp.setDate(point.getX());
                    cp.setNetAmount(point.getY());
                    return cp;
                })
                .collect(Collectors.toList()));
        return resp;
    }

    @Override
    public MonthStatisticsResponse getMonth(Long userId, Integer year, Integer month) {
        YearMonth ym = (year == null || month == null) ? YearMonth.now() : YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<LotteryRecord> records = aggregator.queryRange(userId, start, end);
        StatisticsAggregator.Totals totals = aggregator.aggregate(records);

        MonthStatisticsResponse resp = new MonthStatisticsResponse();
        resp.setTotalInvestAmount(totals.getInvestAmount());
        resp.setTotalProfitAmount(totals.getProfitAmount());
        resp.setTotalNetAmount(totals.getNetAmount());
        resp.setChart(aggregator.fillDailyPoints(start, end, records, false).stream()
                .map(point -> {
                    MonthStatisticsResponse.ChartPoint cp = new MonthStatisticsResponse.ChartPoint();
                    cp.setDate(point.getX());
                    cp.setNetAmount(point.getY());
                    return cp;
                })
                .collect(Collectors.toList()));
        return resp;
    }

    @Override
    public YearStatisticsResponse getYear(Long userId, Integer year) {
        int targetYear = year == null ? LocalDate.now().getYear() : year;
        LocalDate start = LocalDate.of(targetYear, 1, 1);
        LocalDate end = LocalDate.of(targetYear, 12, 31);
        List<LotteryRecord> records = aggregator.queryRange(userId, start, end);

        YearStatisticsResponse resp = new YearStatisticsResponse();
        resp.setYear(targetYear);
        resp.setChart(aggregator.fillMonthlyPoints(targetYear, records, true).stream()
                .map(point -> {
                    YearStatisticsResponse.MonthPoint mp = new YearStatisticsResponse.MonthPoint();
                    mp.setDate(point.getX());
                    mp.setNetAmount(point.getY());
                    return mp;
                })
                .collect(Collectors.toList()));
        return resp;
    }
}
