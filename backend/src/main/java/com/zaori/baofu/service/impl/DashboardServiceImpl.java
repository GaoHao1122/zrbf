package com.zaori.baofu.service.impl;

import com.zaori.baofu.common.ProfitStatus;
import com.zaori.baofu.dto.response.DashboardResponse;
import com.zaori.baofu.entity.LotteryRecord;
import com.zaori.baofu.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StatisticsAggregator aggregator;

    @Override
    public DashboardResponse getDashboard(Long userId) {
        DashboardResponse resp = new DashboardResponse();
        LocalDate today = LocalDate.now();

        // 今日数据：没有记录时返回 0
        DashboardResponse.TodayInfo todayInfo = new DashboardResponse.TodayInfo();
        LotteryRecord todayRecord = aggregator.queryRange(userId, today, today).stream().findFirst().orElse(null);
        if (todayRecord == null) {
            todayInfo.setInvestAmount(BigDecimal.ZERO);
            todayInfo.setNetAmount(BigDecimal.ZERO);
            todayInfo.setProfitRate("0%");
            todayInfo.setStatus("EVEN");
        } else {
            todayInfo.setInvestAmount(todayRecord.getInvestAmount());
            todayInfo.setNetAmount(todayRecord.getNetAmount());
            todayInfo.setProfitRate(todayRecord.getProfitRate() + "%");
            todayInfo.setStatus(ProfitStatus.dbToApi(todayRecord.getStatus()));
        }
        resp.setToday(todayInfo);

        // 累计统计
        List<LotteryRecord> allRecords = aggregator.queryRange(userId, null, null);
        StatisticsAggregator.Totals totals = aggregator.aggregate(allRecords);
        DashboardResponse.SummaryInfo summary = new DashboardResponse.SummaryInfo();
        summary.setTotalInvestAmount(totals.getInvestAmount());
        summary.setTotalProfitAmount(totals.getProfitAmount());
        summary.setTotalNetAmount(totals.getNetAmount());
        summary.setWinRate(totals.getWinRate());
        resp.setSummary(summary);

        // 最近 7 天趋势预览
        LocalDate start = today.minusDays(6);
        List<LotteryRecord> weekRecords = aggregator.queryRange(userId, start, today);
        DashboardResponse.ChartInfo chart = new DashboardResponse.ChartInfo();
        chart.setType("week");
        chart.setData(aggregator.fillDailyPoints(start, today, weekRecords, false).stream()
                .map(point -> {
                    DashboardResponse.ChartPoint cp = new DashboardResponse.ChartPoint();
                    cp.setX(point.getX());
                    cp.setY(point.getY());
                    return cp;
                })
                .collect(Collectors.toList()));
        resp.setChart(chart);
        return resp;
    }
}
